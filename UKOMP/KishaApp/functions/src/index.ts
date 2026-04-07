import * as admin from "firebase-admin";
import * as functions from "firebase-functions";

// Initialize Firebase Admin SDK
admin.initializeApp();

const db = admin.firestore();
const auth = admin.auth();

// ============ USER FUNCTIONS ============

/**
 * Cloud Function: Validate buyer/customer profile
 * Trigger: HTTP callable from Android app
 * Purpose: Server-side validation untuk user profile
 */
export const validateUserProfile = functions.https.onCall(
  async (data, context) => {
    // Check if user is authenticated
    if (!context.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "User harus login",
      );
    }

    const uid = context.auth.uid;
    const {name, email, role} = data;

    // Validation
    if (!name || name.trim().length < 2) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Nama harus minimal 2 karakter",
      );
    }

    if (!email || !email.includes("@")) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Email tidak valid",
      );
    }

    // Update user profile di Firestore
    try {
      await db.collection("users").doc(uid).update({
        name: name.trim(),
        email: email.trim(),
        role: role || "customer",
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      return {
        success: true,
        message: "Profile berhasil divalidasi",
        uid,
      };
    } catch (error) {
      throw new functions.https.HttpsError(
        "internal",
        "Gagal update profile",
      );
    }
  },
);

// ============ PRODUCT FUNCTIONS ============

/**
 * Cloud Function: Validate seller product
 * Trigger: HTTP callable from Android app
 * Purpose: Server-side validation untuk produk sebelum save
 */
export const validateProduct = functions.https.onCall(
  async (data, context) => {
    if (!context.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "User harus login",
      );
    }

    const uid = context.auth.uid;
    const {name, description, price, category, stock} = data;

    // Check seller role
    const userDoc = await db.collection("users").doc(uid).get();
    const userData = userDoc.data();

    if (userData?.role !== "seller" && userData?.role !== "admin") {
      throw new functions.https.HttpsError(
        "permission-denied",
        "Hanya seller yang bisa add product",
      );
    }

    // Validation
    if (!name || name.trim().length < 3) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Nama produk minimal 3 karakter",
      );
    }

    if (!description || description.trim().length < 10) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Deskripsi minimal 10 karakter",
      );
    }

    if (!price || price < 0) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Harga tidak valid",
      );
    }

    if (!stock || stock < 0) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Stock tidak valid",
      );
    }

    return {
      success: true,
      message: "Produk valid",
      isValid: true,
    };
  },
);

/**
 * Cloud Function: Create product (with automatic seller assignment)
 * Trigger: HTTP callable
 * Purpose: Buat produk baru dengan validasi server-side
 */
export const createProduct = functions.https.onCall(
  async (data, context) => {
    if (!context.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "User harus login",
      );
    }

    const uid = context.auth.uid;
    const {name, description, price, category, stock, imageUrl} = data;

    // Check seller role
    const userDoc = await db.collection("users").doc(uid).get();
    const userData = userDoc.data();

    if (userData?.role !== "seller" && userData?.role !== "admin") {
      throw new functions.https.HttpsError(
        "permission-denied",
        "Hanya seller yang bisa buat product",
      );
    }

    // Validate data
    if (!name?.trim() || name.length < 3) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Nama produk tidak valid",
      );
    }

    try {
      const productRef = db.collection("products").doc();
      const productData = {
        id: productRef.id,
        name: name.trim(),
        description: description?.trim() || "",
        price: Number(price) || 0,
        category: category?.trim() || "Umum",
        stock: Number(stock) || 0,
        imageUrl: imageUrl || "",
        sellerId: uid,
        sellerName: userData?.name || "Penjual",
        status: "active",
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        ratings: 0,
        reviewCount: 0,
      };

      await productRef.set(productData);

      return {
        success: true,
        message: "Produk berhasil dibuat",
        productId: productRef.id,
        product: productData,
      };
    } catch (error) {
      throw new functions.https.HttpsError(
        "internal",
        `Gagal buat produk: ${error}`,
      );
    }
  },
);

// ============ ORDER FUNCTIONS ============

/**
 * Cloud Function: Create order
 * Trigger: HTTP callable
 * Purpose: Buat order baru dengan validasi inventory
 */
export const createOrder = functions.https.onCall(
  async (data, context) => {
    if (!context.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "User harus login",
      );
    }

    const uid = context.auth.uid;
    const {productId, quantity, totalPrice} = data;

    if (!productId || !quantity || quantity <= 0) {
      throw new functions.https.HttpsError(
        "invalid-argument",
        "Data order tidak valid",
      );
    }

    try {
      // Check product existence dan stock
      const productDoc = await db.collection("products").doc(productId).get();

      if (!productDoc.exists) {
        throw new functions.https.HttpsError(
          "not-found",
          "Produk tidak ditemukan",
        );
      }

      const productData = productDoc.data();
      if (productData?.stock < quantity) {
        throw new functions.https.HttpsError(
          "failed-precondition",
          "Stock tidak cukup",
        );
      }

      // Create order
      const orderRef = db.collection("orders").doc();
      const orderData = {
        id: orderRef.id,
        buyerId: uid,
        productId: productId,
        productName: productData?.name,
        sellerId: productData?.sellerId,
        quantity: quantity,
        totalPrice: Number(totalPrice) || 0,
        status: "pending",
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      };

      await orderRef.set(orderData);

      // Update product stock
      await db.collection("products").doc(productId).update({
        stock: (productData?.stock || 0) - quantity,
      });

      return {
        success: true,
        message: "Order berhasil dibuat",
        orderId: orderRef.id,
        order: orderData,
      };
    } catch (error) {
      throw new functions.https.HttpsError(
        "internal",
        `Gagal buat order: ${error}`,
      );
    }
  },
);

// ============ ADMIN FUNCTIONS ============

/**
 * Cloud Function: Get statistics
 * Trigger: HTTP callable (admin only)
 * Purpose: Get marketplace statistics
 */
export const getStatistics = functions.https.onCall(
  async (data, context) => {
    if (!context.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "User harus login",
      );
    }

    const uid = context.auth.uid;

    // Check admin role
    const userDoc = await db.collection("users").doc(uid).get();
    const userData = userDoc.data();

    if (userData?.role !== "admin") {
      throw new functions.https.HttpsError(
        "permission-denied",
        "Hanya admin yang bisa akses statistik",
      );
    }

    try {
      const usersCount = (await db.collection("users").count().get()).data().count;
      const productsCount = (
        await db.collection("products").count().get()
      ).data().count;
      const ordersCount = (
        await db.collection("orders").count().get()
      ).data().count;

      const totalRevenue = (
        await db.collection("orders").where("status", "==", "completed").get()
      ).docs.reduce(
        (sum, doc) => sum + (doc.data().totalPrice || 0),
        0,
      );

      return {
        success: true,
        statistics: {
          totalUsers: usersCount,
          totalProducts: productsCount,
          totalOrders: ordersCount,
          totalRevenue: totalRevenue,
        },
      };
    } catch (error) {
      throw new functions.https.HttpsError(
        "internal",
        `Gagal ambil statistik: ${error}`,
      );
    }
  },
);

// ============ UTILITY FUNCTIONS ============

/**
 * Background Function: Process order completion
 * Trigger: Firestore document write
 * Purpose: Update product rating ketika order selesai
 */
export const onOrderCompleted = functions.firestore
  .document("orders/{orderId}")
  .onUpdate(async (change) => {
    const before = change.before.data();
    const after = change.after.data();

    // Only process if status changed to completed
    if (before.status !== "completed" && after.status === "completed") {
      try {
        const productRef = db.collection("products").doc(after.productId);
        await productRef.update({
          salesCount: admin.firestore.FieldValue.increment(after.quantity),
        });

        functions.logger.info(
          `Order ${change.after.id} completed`,
        );
      } catch (error) {
        functions.logger.error(`Error processing order: ${error}`);
      }
    }
  });

/**
 * Background Function: Cleanup Firestore user document when Auth user is deleted
 * Trigger: Firebase Authentication user delete
 * Purpose: Prevent orphaned users/{uid} documents in Firestore
 */
export const onAuthUserDeleted = functions.auth.user().onDelete(async (user) => {
  const uid = user.uid;

  try {
    await db.collection("users").doc(uid).delete();
    functions.logger.info(`Firestore users/${uid} deleted after Auth deletion`);
  } catch (error) {
    functions.logger.error(`Failed to delete Firestore users/${uid}: ${error}`);
  }
});

export const helloWorld = functions.https.onRequest(
  (_request, response) => {
    response.send("Hello from Firebase Cloud Functions!");
  },
);
