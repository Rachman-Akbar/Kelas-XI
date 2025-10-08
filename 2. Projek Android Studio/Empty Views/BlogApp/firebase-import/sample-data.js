// Sample users data to import
const sampleUsers = [
  {
    id: 'user-demo-1',
    email: 'demo@blogapp.com',    authorName: 'developer@tech.com',
    timestamp: Date.now() - (12 * 60 * 60 * 1000), // 12 hours ago
    imageUrl: ''
  },
  {
    id: 'blog-7',
    title: 'Resep Nasi Gudeg Jogja Asli',
    content: 'Gudeg adalah makanan khas Yogyakarta yang terbuat dari nangka muda. Cara membuatnya: 1) Rebus nangka muda dengan santan, 2) Tambahkan bumbu halus (bawang merah, putih, kemiri, ketumbar), 3) Masak dengan api kecil selama 3-4 jam, 4) Sajikan dengan nasi, ayam, telur, dan sambal krecek.',
    authorId: 'user-demo-5',
    authorName: 'foodie@culinary.com',
    timestamp: Date.now() - (8 * 60 * 60 * 1000), // 8 hours ago
    imageUrl: ''
  },
  {
    id: 'blog-8',
    title: 'Cerita Inspiratif: Dari Nol Menjadi Entrepreneur',
    content: 'Perjalanan menjadi entrepreneur tidaklah mudah. Dimulai dari ide sederhana, modal pas-pasan, hingga menghadapi berbagai tantangan. Yang terpenting adalah konsistensi, pantang menyerah, dan terus belajar dari setiap kegagalan. Setiap masalah adalah kesempatan untuk berkembang.',
    authorId: 'user-demo-4',
    authorName: 'writer@stories.com',
    timestamp: Date.now() - (6 * 60 * 60 * 1000), // 6 hours ago
    imageUrl: ''
  },
  {
    id: 'blog-9',
    title: 'Setup Development Environment untuk Android',
    content: 'Langkah-langkah setup environment Android development: 1) Install Android Studio, 2) Setup Android SDK, 3) Configure emulator atau physical device, 4) Install Git untuk version control, 5) Setup Firebase project, 6) Configure Gradle dan dependencies yang diperlukan.',
    authorId: 'user-demo-3',
    authorName: 'developer@tech.com',
    timestamp: Date.now() - (4 * 60 * 60 * 1000), // 4 hours ago
    imageUrl: ''
  },
  {
    id: 'blog-10',
    title: 'Tips Traveling Budget ke Bali',
    content: 'Traveling ke Bali dengan budget terbatas bisa dilakukan dengan tips berikut: 1) Pilih homestay atau guesthouse, 2) Makan di warung lokal, 3) Gunakan transportasi umum atau sewa motor, 4) Kunjungi tempat wisata gratis seperti pantai dan pura, 5) Beli oleh-oleh di pasar tradisional.',
    authorId: 'user-demo-5',
    authorName: 'foodie@culinary.com',
    timestamp: Date.now() - (2 * 60 * 60 * 1000), // 2 hours ago
    imageUrl: ''
  },
  {
    id: 'blog-11',
    title: 'Manfaat Meditasi untuk Produktivitas',
    content: 'Meditasi bukan hanya untuk relaksasi, tetapi juga meningkatkan produktivitas. Manfaat meditasi: 1) Meningkatkan fokus dan konsentrasi, 2) Mengurangi stress dan anxiety, 3) Meningkatkan kreativitas, 4) Memperbaiki kualitas tidur, 5) Meningkatkan emotional intelligence.',
    authorId: 'user-demo-4',
    authorName: 'writer@stories.com',
    timestamp: Date.now() - (1 * 60 * 60 * 1000), // 1 hour ago
    imageUrl: ''
  },
  {
    id: 'blog-12',
    title: 'Best Practices Firebase Security Rules',
    content: 'Keamanan Firebase Firestore sangat penting. Best practices: 1) Jangan gunakan test mode di production, 2) Implement authentication terlebih dahulu, 3) Gunakan resource-based rules, 4) Validate data dengan schema, 5) Test security rules secara menyeluruh, 6) Monitor activity logs.',
    authorId: 'user-demo-1',
    authorName: 'demo@blogapp.com',
    timestamp: Date.now() - (30 * 60 * 1000), // 30 minutes ago
    imageUrl: ''
  }
];

// Sample categories data
const sampleCategories = [
  {
    id: 'cat-1',
    name: 'Technology',
    description: 'Artikel tentang teknologi, programming, dan development',
    icon: '💻',
    color: '#2196F3',
    createdDate: Date.now() - (30 * 24 * 60 * 60 * 1000),
    isActive: true,
    totalBlogs: 0
  },
  {
    id: 'cat-2',
    name: 'Lifestyle',
    description: 'Tips dan trik untuk gaya hidup sehat dan produktif',
    icon: '🌟',
    color: '#FF9800',
    createdDate: Date.now() - (25 * 24 * 60 * 60 * 1000),
    isActive: true,
    totalBlogs: 0
  },
  {
    id: 'cat-3',
    name: 'Food & Travel',
    description: 'Kuliner dan destinasi wisata menarik',
    icon: '🍜',
    color: '#4CAF50',
    createdDate: Date.now() - (20 * 24 * 60 * 60 * 1000),
    isActive: true,
    totalBlogs: 0
  },
  {
    id: 'cat-4',
    name: 'Business',
    description: 'Tips bisnis, entrepreneurship, dan karir',
    icon: '💼',
    color: '#9C27B0',
    createdDate: Date.now() - (15 * 24 * 60 * 60 * 1000),
    isActive: true,
    totalBlogs: 0
  },
  {
    id: 'cat-5',
    name: 'Personal Development',
    description: 'Pengembangan diri dan motivasi',
    icon: '🚀',
    color: '#E91E63',
    createdDate: Date.now() - (10 * 24 * 60 * 60 * 1000),
    isActive: true,
    totalBlogs: 0
  }
];

// Sample comments data
const sampleComments = [
  {
    id: 'comment-1',
    blogId: 'blog-1',
    authorId: 'user-demo-2',
    authorName: 'blogger@example.com',
    content: 'Great article! Sangat membantu untuk pemula seperti saya.',
    timestamp: Date.now() - (6 * 24 * 60 * 60 * 1000),
    isApproved: true
  },
  {
    id: 'comment-2',
    blogId: 'blog-1',
    authorId: 'user-demo-3',
    authorName: 'developer@tech.com',
    content: 'Setuju banget! BlogApp memang user-friendly.',
    timestamp: Date.now() - (5 * 24 * 60 * 60 * 1000),
    isApproved: true
  },
  {
    id: 'comment-3',
    blogId: 'blog-2',
    authorId: 'user-demo-4',
    authorName: 'writer@stories.com',
    content: 'Tips yang sangat berguna. Saya sudah praktek dan hasilnya bagus!',
    timestamp: Date.now() - (4 * 24 * 60 * 60 * 1000),
    isApproved: true
  },
  {
    id: 'comment-4',
    blogId: 'blog-3',
    authorId: 'user-demo-1',
    authorName: 'demo@blogapp.com',
    content: 'Flutter memang the future of mobile development. Thanks for sharing!',
    timestamp: Date.now() - (2 * 24 * 60 * 60 * 1000),
    isApproved: true
  },
  {
    id: 'comment-5',
    blogId: 'blog-7',
    authorId: 'user-demo-4',
    authorName: 'writer@stories.com',
    content: 'Resepnya detail banget! Mau coba bikin weekend ini.',
    timestamp: Date.now() - (6 * 60 * 60 * 1000),
    isApproved: true
  }
];

// Sample tags data
const sampleTags = [
  {
    id: 'tag-1',
    name: 'android',
    color: '#4CAF50',
    usageCount: 0
  },
  {
    id: 'tag-2',
    name: 'kotlin',
    color: '#FF9800',
    usageCount: 0
  },
  {
    id: 'tag-3',
    name: 'firebase',
    color: '#FF5722',
    usageCount: 0
  },
  {
    id: 'tag-4',
    name: 'programming',
    color: '#2196F3',
    usageCount: 0
  },
  {
    id: 'tag-5',
    name: 'tutorial',
    color: '#9C27B0',
    usageCount: 0
  },
  {
    id: 'tag-6',
    name: 'food',
    color: '#795548',
    usageCount: 0
  },
  {
    id: 'tag-7',
    name: 'travel',
    color: '#00BCD4',
    usageCount: 0
  },
  {
    id: 'tag-8',
    name: 'lifestyle',
    color: '#E91E63',
    usageCount: 0
  },
  {
    id: 'tag-9',
    name: 'business',
    color: '#607D8B',
    usageCount: 0
  },
  {
    id: 'tag-10',
    name: 'inspiration',
    color: '#CDDC39',
    usageCount: 0
  }
];

module.exports = { sampleBlogs, sampleUsers, sampleCategories, sampleComments, sampleTags };isplayName: 'Demo User',
    bio: 'Seorang blogger pemula yang senang berbagi pengalaman sehari-hari.',
    profileImageUrl: '',
    joinedDate: Date.now() - (30 * 24 * 60 * 60 * 1000), // 30 days ago
    isActive: true,
    totalBlogs: 0,
    location: 'Jakarta, Indonesia'
  },
  {
    id: 'user-demo-2', 
    email: 'blogger@example.com',
    displayName: 'Professional Blogger',
    bio: 'Content creator dan blogger profesional dengan 5+ tahun pengalaman.',
    profileImageUrl: '',
    joinedDate: Date.now() - (25 * 24 * 60 * 60 * 1000), // 25 days ago
    isActive: true,
    totalBlogs: 0,
    location: 'Bandung, Indonesia'
  },
  {
    id: 'user-demo-3',
    email: 'developer@tech.com', 
    displayName: 'Tech Developer',
    bio: 'Mobile developer yang passionate tentang teknologi dan inovasi.',
    profileImageUrl: '',
    joinedDate: Date.now() - (20 * 24 * 60 * 60 * 1000), // 20 days ago
    isActive: true,
    totalBlogs: 0,
    location: 'Surabaya, Indonesia'
  },
  {
    id: 'user-demo-4',
    email: 'writer@stories.com',
    displayName: 'Creative Writer',
    bio: 'Penulis kreatif yang suka berbagi cerita inspiratif.',
    profileImageUrl: '',
    joinedDate: Date.now() - (15 * 24 * 60 * 60 * 1000), // 15 days ago
    isActive: true,
    totalBlogs: 0,
    location: 'Yogyakarta, Indonesia'
  },
  {
    id: 'user-demo-5',
    email: 'foodie@culinary.com',
    displayName: 'Food Enthusiast',
    bio: 'Food blogger yang explore kuliner nusantara.',
    profileImageUrl: '',
    joinedDate: Date.now() - (10 * 24 * 60 * 60 * 1000), // 10 days ago
    isActive: true,
    totalBlogs: 0,
    location: 'Bali, Indonesia'
  }
];

// Sample blog data to import
const sampleBlogs = [
  {
    id: 'blog-1',
    title: 'Welcome to BlogApp',
    content: 'Selamat datang di BlogApp! Aplikasi ini memungkinkan Anda untuk berbagi cerita dan pengalaman dengan komunitas. Anda dapat menulis blog tentang topik apa saja yang Anda minati.',
    authorId: 'user-demo-1',
    authorName: 'demo@blogapp.com',
    timestamp: Date.now() - 7 * 24 * 60 * 60 * 1000, // 7 days ago
    imageUrl: '',
  },
  {
    id: 'blog-2',
    title: 'Tips Menulis Blog yang Menarik',
    content:
      'Menulis blog yang menarik memerlukan beberapa tips: 1) Pilih topik yang Anda kuasai, 2) Gunakan judul yang catchy, 3) Buat pembukaan yang menarik perhatian, 4) Gunakan paragraf pendek agar mudah dibaca, 5) Tambahkan contoh atau pengalaman pribadi.',
    authorId: 'user-demo-2',
    authorName: 'blogger@example.com',
    timestamp: Date.now() - 5 * 24 * 60 * 60 * 1000, // 5 days ago
    imageUrl: '',
  },
  {
    id: 'blog-3',
    title: 'Teknologi Mobile Development di 2025',
    content: 'Perkembangan teknologi mobile development di tahun 2025 sangat pesat. Kotlin untuk Android dan Swift untuk iOS masih menjadi pilihan utama. Framework cross-platform seperti Flutter dan React Native juga semakin populer.',
    authorId: 'user-demo-3',
    authorName: 'developer@tech.com',
    timestamp: Date.now() - 3 * 24 * 60 * 60 * 1000, // 3 days ago
    imageUrl: '',
  },
  {
    id: 'blog-4',
    title: 'Pentingnya Firebase dalam Aplikasi Modern',
    content:
      'Firebase menyediakan berbagai layanan backend yang sangat membantu developer: Authentication, Firestore Database, Cloud Storage, Analytics, dan masih banyak lagi. Dengan Firebase, developer bisa fokus pada pengembangan fitur tanpa perlu mengurus server.',
    authorId: 'user-demo-1',
    authorName: 'demo@blogapp.com',
    timestamp: Date.now() - 2 * 24 * 60 * 60 * 1000, // 2 days ago
    imageUrl: '',
  },
  {
    id: 'blog-5',
    title: 'Belajar Kotlin untuk Pemula',
    content:
      'Kotlin adalah bahasa pemrograman modern yang 100% interoperable dengan Java. Kotlin memiliki syntax yang lebih concise dan safe dibanding Java. Fitur-fitur seperti null safety, extension functions, dan coroutines membuat Kotlin sangat powerful.',
    authorId: 'user-demo-2',
    authorName: 'blogger@example.com',
    timestamp: Date.now() - 1 * 24 * 60 * 60 * 1000, // 1 day ago
    imageUrl: '',
  },
  {
    id: 'blog-6',
    title: 'UI/UX Design Trends 2025',
    content: 'Tren desain UI/UX di 2025: 1) Dark mode sebagai standar, 2) Micro-interactions yang smooth, 3) Voice interface integration, 4) AR/VR elements, 5) Sustainable design practices, 6) Accessibility-first approach.',
    authorId: 'user-demo-3',
    authorName: 'developer@tech.com',
    timestamp: Date.now() - 12 * 60 * 60 * 1000, // 12 hours ago
    imageUrl: '',
  },
];

module.exports = { sampleBlogs };
