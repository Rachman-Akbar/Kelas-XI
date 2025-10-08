// Sample user data for import
const sampleUsers = [
  {
    username: 'john_doe',
    email: 'john.doe@example.com',
    password: 'password123',
    bio: 'Photographer and traveler 📸✈️',
    profileImageUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
    followersCount: 1250,
    followingCount: 680,
    postsCount: 45,
    isPrivate: false,
  },
  {
    username: 'jane_smith',
    email: 'jane.smith@example.com',
    password: 'password123',
    bio: 'Food blogger & chef 🍳👩‍🍳',
    profileImageUrl: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face',
    followersCount: 2100,
    followingCount: 420,
    postsCount: 78,
    isPrivate: false,
  },
  {
    username: 'mike_wilson',
    email: 'mike.wilson@example.com',
    password: 'password123',
    bio: 'Fitness enthusiast 💪 Motivating others daily',
    profileImageUrl: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
    followersCount: 3400,
    followingCount: 890,
    postsCount: 156,
    isPrivate: false,
  },
  {
    username: 'sarah_johnson',
    email: 'sarah.johnson@example.com',
    password: 'password123',
    bio: 'Artist & Designer 🎨 Creating beautiful things',
    profileImageUrl: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face',
    followersCount: 1850,
    followingCount: 320,
    postsCount: 92,
    isPrivate: false,
  },
  {
    username: 'alex_brown',
    email: 'alex.brown@example.com',
    password: 'password123',
    bio: 'Tech entrepreneur 💻 Building the future',
    profileImageUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face',
    followersCount: 5200,
    followingCount: 1200,
    postsCount: 203,
    isPrivate: false,
  },
  {
    username: 'emma_davis',
    email: 'emma.davis@example.com',
    password: 'password123',
    bio: 'Yoga instructor 🧘‍♀️ Mindfulness & wellness',
    profileImageUrl: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150&h=150&fit=crop&crop=face',
    followersCount: 2750,
    followingCount: 540,
    postsCount: 134,
    isPrivate: false,
  },
  {
    username: 'david_garcia',
    email: 'david.garcia@example.com',
    password: 'password123',
    bio: 'Music producer 🎵 Creating beats that move souls',
    profileImageUrl: 'https://images.unsplash.com/photo-1507591064344-4c6ce005b128?w=150&h=150&fit=crop&crop=face',
    followersCount: 4100,
    followingCount: 780,
    postsCount: 187,
    isPrivate: false,
  },
  {
    username: 'lisa_martinez',
    email: 'lisa.martinez@example.com',
    password: 'password123',
    bio: 'Fashion stylist 👗 Helping you look your best',
    profileImageUrl: 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?w=150&h=150&fit=crop&crop=face',
    followersCount: 6800,
    followingCount: 1100,
    postsCount: 298,
    isPrivate: false,
  },
  {
    username: 'chris_lee',
    email: 'chris.lee@example.com',
    password: 'password123',
    bio: 'Adventure seeker 🏔️ Life is meant to be explored',
    profileImageUrl: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150&h=150&fit=crop&crop=face',
    followersCount: 3200,
    followingCount: 920,
    postsCount: 167,
    isPrivate: false,
  },
  {
    username: 'rachel_taylor',
    email: 'rachel.taylor@example.com',
    password: 'password123',
    bio: 'Book lover 📚 Sharing stories that inspire',
    profileImageUrl: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&h=150&fit=crop&crop=face',
    followersCount: 1950,
    followingCount: 430,
    postsCount: 89,
    isPrivate: false,
  },
];

// Sample post data
const samplePosts = [
  {
    imageUrl: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=500&h=500&fit=crop',
    caption: 'Beautiful sunset at the beach 🌅 Perfect end to a perfect day!',
    likesCount: 156,
    commentsCount: 23,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=500&h=500&fit=crop',
    caption: 'Homemade pasta night 🍝 Nothing beats fresh ingredients!',
    likesCount: 89,
    commentsCount: 12,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=500&h=500&fit=crop',
    caption: 'Morning workout complete 💪 Starting the day right!',
    likesCount: 234,
    commentsCount: 45,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=500&h=500&fit=crop',
    caption: 'New art piece finished! 🎨 What do you think?',
    likesCount: 312,
    commentsCount: 67,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=500&h=500&fit=crop',
    caption: 'Late night coding session ⌨️ Building something amazing!',
    likesCount: 178,
    commentsCount: 28,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=500&h=500&fit=crop',
    caption: 'Morning yoga session 🧘‍♀️ Finding inner peace',
    likesCount: 145,
    commentsCount: 19,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&h=500&fit=crop',
    caption: "New track in the works 🎵 Can't wait to share it!",
    likesCount: 267,
    commentsCount: 51,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=500&h=500&fit=crop',
    caption: 'Fashion week vibes ✨ Loving this new collection!',
    likesCount: 423,
    commentsCount: 78,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=500&h=500&fit=crop',
    caption: 'Mountain hiking adventure 🏔️ The view was worth every step!',
    likesCount: 198,
    commentsCount: 34,
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=500&h=500&fit=crop',
    caption: 'Currently reading this amazing book 📖 Highly recommend!',
    likesCount: 92,
    commentsCount: 15,
  },
];

// Pre-loaded images untuk story dan post upload
const preloadedImages = {
  stories: [
    {
      id: 'story_nature_1',
      url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=600&fit=crop',
      category: 'nature',
      title: 'Beautiful Sunset',
      description: 'Golden hour at the beach',
    },
    {
      id: 'story_food_1',
      url: 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=400&h=600&fit=crop',
      category: 'food',
      title: 'Delicious Food',
      description: 'Gourmet meal preparation',
    },
    {
      id: 'story_fitness_1',
      url: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=600&fit=crop',
      category: 'fitness',
      title: 'Workout Time',
      description: 'Morning exercise routine',
    },
    {
      id: 'story_city_1',
      url: 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=400&h=600&fit=crop',
      category: 'city',
      title: 'Urban Life',
      description: 'City skyline at night',
    },
    {
      id: 'story_coffee_1',
      url: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&h=600&fit=crop',
      category: 'lifestyle',
      title: 'Coffee Break',
      description: 'Perfect morning coffee',
    },
    {
      id: 'story_travel_1',
      url: 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=400&h=600&fit=crop',
      category: 'travel',
      title: 'Adventure',
      description: 'Mountain hiking trail',
    },
    {
      id: 'story_art_1',
      url: 'https://images.unsplash.com/photo-1460661419201-fd4cecdf8a8b?w=400&h=600&fit=crop',
      category: 'art',
      title: 'Creative Work',
      description: 'Artist workspace',
    },
    {
      id: 'story_music_1',
      url: 'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400&h=600&fit=crop',
      category: 'music',
      title: 'Music Studio',
      description: 'Recording session',
    },
    {
      id: 'story_fashion_1',
      url: 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=400&h=600&fit=crop',
      category: 'fashion',
      title: 'Style Inspiration',
      description: 'Fashion photoshoot',
    },
    {
      id: 'story_book_1',
      url: 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop',
      category: 'books',
      title: 'Reading Time',
      description: 'Cozy reading corner',
    },
    {
      id: 'story_pets_1',
      url: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400&h=600&fit=crop',
      category: 'pets',
      title: 'Cute Pet',
      description: 'Adorable golden retriever',
    },
    {
      id: 'story_nature_2',
      url: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=600&fit=crop',
      category: 'nature',
      title: 'Forest Path',
      description: 'Peaceful forest walk',
    },
  ],
  posts: [
    {
      id: 'post_landscape_1',
      url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600&h=600&fit=crop',
      category: 'landscape',
      title: 'Stunning Landscape',
      description: 'Perfect for post sharing',
    },
    {
      id: 'post_food_1',
      url: 'https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=600&h=600&fit=crop',
      category: 'food',
      title: 'Food Photography',
      description: 'Delicious meal',
    },
    {
      id: 'post_fitness_1',
      url: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600&h=600&fit=crop',
      category: 'fitness',
      title: 'Fitness Motivation',
      description: 'Workout inspiration',
    },
    {
      id: 'post_city_1',
      url: 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=600&h=600&fit=crop',
      category: 'city',
      title: 'City Vibes',
      description: 'Urban photography',
    },
    {
      id: 'post_coffee_1',
      url: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600&h=600&fit=crop',
      category: 'lifestyle',
      title: 'Coffee Art',
      description: 'Perfect latte art',
    },
    {
      id: 'post_travel_1',
      url: 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=600&h=600&fit=crop',
      category: 'travel',
      title: 'Travel Adventure',
      description: 'Exploring new places',
    },
  ],
  backgrounds: [
    { id: 'bg_gradient_1', color: '#FF6B6B', name: 'Coral Red' },
    { id: 'bg_gradient_2', color: '#4ECDC4', name: 'Turquoise' },
    { id: 'bg_gradient_3', color: '#45B7D1', name: 'Sky Blue' },
    { id: 'bg_gradient_4', color: '#96CEB4', name: 'Mint Green' },
    { id: 'bg_gradient_5', color: '#FFEAA7', name: 'Sunny Yellow' },
    { id: 'bg_gradient_6', color: '#DDA0DD', name: 'Plum' },
    { id: 'bg_gradient_7', color: '#98D8C8', name: 'Seafoam' },
    { id: 'bg_gradient_8', color: '#F7DC6F', name: 'Golden' },
    { id: 'bg_gradient_9', color: '#BB8FCE', name: 'Lavender' },
    { id: 'bg_gradient_10', color: '#85C1E9', name: 'Light Blue' },
  ],
};

// Sample story data
const sampleStories = [
  {
    imageUrl: preloadedImages.stories[0].url,
    text: '',
    backgroundColor: '#000000',
  },
  {
    imageUrl: preloadedImages.stories[1].url,
    text: '',
    backgroundColor: '#000000',
  },
  {
    imageUrl: preloadedImages.stories[2].url,
    text: '',
    backgroundColor: '#000000',
  },
  {
    imageUrl: '',
    text: 'Having a great day! ☀️',
    backgroundColor: '#FF6B6B',
  },
  {
    imageUrl: '',
    text: 'Coffee time ☕',
    backgroundColor: '#4ECDC4',
  },
];

// Helper functions
function generateRandomComment() {
  const comments = ['Great post! 👍', 'Love this! ❤️', 'Amazing! 🔥', 'So cool! 😍', 'Awesome content! 🌟', 'Beautiful! ✨', 'Incredible! 🤩', 'Nice shot! 📸', 'Well done! 👏', 'Fantastic! 🎉'];
  return comments[Math.floor(Math.random() * comments.length)];
}

function generateRandomPostContent() {
  const contents = [
    'Having a great day!',
    'Beautiful sunset today',
    'Coffee time ☕',
    'Working on something amazing',
    'Life is good 😊',
    'Grateful for this moment',
    'Adventure awaits!',
    'Making memories',
    'Feeling inspired',
    'Good vibes only ✨',
  ];
  return contents[Math.floor(Math.random() * contents.length)];
}

module.exports = {
  sampleUsers,
  samplePosts,
  sampleStories,
  preloadedImages,
  generateRandomComment,
  generateRandomPostContent,
};
