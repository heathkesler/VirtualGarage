-- V003: Add community features (Users, Forum, Part Suggestions, Videos)

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(500),
    bio TEXT,
    location VARCHAR(100),
    reputation_score INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_moderator BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_seen_at TIMESTAMP
);

CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_reputation ON users(reputation_score);

-- Add user_id to vehicles table
ALTER TABLE vehicles ADD COLUMN user_id BIGINT REFERENCES users(id);
CREATE INDEX idx_vehicle_user ON vehicles(user_id);

-- Forum categories
CREATE TABLE forum_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon_name VARCHAR(50),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    thread_count INTEGER NOT NULL DEFAULT 0,
    post_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_forum_category_slug ON forum_categories(slug);
CREATE INDEX idx_forum_category_order ON forum_categories(display_order);

-- Forum threads
CREATE TABLE forum_threads (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    category_id BIGINT NOT NULL REFERENCES forum_categories(id),
    author_id BIGINT NOT NULL REFERENCES users(id),
    vehicle_id BIGINT REFERENCES vehicles(id),
    view_count INTEGER NOT NULL DEFAULT 0,
    reply_count INTEGER NOT NULL DEFAULT 0,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    is_solved BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    thread_type VARCHAR(30) NOT NULL DEFAULT 'DISCUSSION',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP,
    last_post_author_id BIGINT REFERENCES users(id)
);

CREATE INDEX idx_forum_thread_category ON forum_threads(category_id);
CREATE INDEX idx_forum_thread_author ON forum_threads(author_id);
CREATE INDEX idx_forum_thread_created ON forum_threads(created_at);
CREATE INDEX idx_forum_thread_last_activity ON forum_threads(last_activity_at);
CREATE INDEX idx_forum_thread_pinned ON forum_threads(is_pinned);
CREATE INDEX idx_forum_thread_type ON forum_threads(thread_type);

-- Forum posts (replies)
CREATE TABLE forum_posts (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    thread_id BIGINT NOT NULL REFERENCES forum_threads(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users(id),
    reply_to_id BIGINT REFERENCES forum_posts(id),
    upvote_count INTEGER NOT NULL DEFAULT 0,
    downvote_count INTEGER NOT NULL DEFAULT 0,
    is_accepted_answer BOOLEAN NOT NULL DEFAULT FALSE,
    is_edited BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_forum_post_thread ON forum_posts(thread_id);
CREATE INDEX idx_forum_post_author ON forum_posts(author_id);
CREATE INDEX idx_forum_post_created ON forum_posts(created_at);
CREATE INDEX idx_forum_post_accepted ON forum_posts(is_accepted_answer);

-- Part suggestions from community
CREATE TABLE part_suggestions (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id),
    part_name VARCHAR(255) NOT NULL,
    part_number VARCHAR(100),
    brand VARCHAR(100) NOT NULL,
    description TEXT,
    part_category VARCHAR(100) NOT NULL,
    vehicle_make VARCHAR(100),
    vehicle_model VARCHAR(100),
    year_start INTEGER,
    year_end INTEGER,
    engine_type VARCHAR(100),
    price_paid DECIMAL(10, 2),
    purchase_url VARCHAR(500),
    where_to_buy VARCHAR(255),
    installation_difficulty VARCHAR(20),
    installation_time_minutes INTEGER,
    installation_notes TEXT,
    tools_required TEXT,
    average_rating DECIMAL(3, 2) DEFAULT 0,
    rating_count INTEGER NOT NULL DEFAULT 0,
    upvote_count INTEGER NOT NULL DEFAULT 0,
    downvote_count INTEGER NOT NULL DEFAULT 0,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_part_suggestion_author ON part_suggestions(author_id);
CREATE INDEX idx_part_suggestion_vehicle ON part_suggestions(vehicle_make, vehicle_model);
CREATE INDEX idx_part_suggestion_category ON part_suggestions(part_category);
CREATE INDEX idx_part_suggestion_rating ON part_suggestions(average_rating);
CREATE INDEX idx_part_suggestion_created ON part_suggestions(created_at);

-- Instructional videos
CREATE TABLE instructional_videos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    video_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    source VARCHAR(30) NOT NULL DEFAULT 'YOUTUBE',
    video_id VARCHAR(100),
    duration_seconds INTEGER,
    category VARCHAR(100) NOT NULL,
    part_type VARCHAR(100),
    vehicle_make VARCHAR(100),
    vehicle_model VARCHAR(100),
    year_start INTEGER,
    year_end INTEGER,
    engine_type VARCHAR(100),
    submitted_by_id BIGINT REFERENCES users(id),
    part_suggestion_id BIGINT REFERENCES part_suggestions(id),
    average_rating DECIMAL(3, 2) DEFAULT 0,
    rating_count INTEGER NOT NULL DEFAULT 0,
    view_count INTEGER NOT NULL DEFAULT 0,
    difficulty_level VARCHAR(20),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_video_submitter ON instructional_videos(submitted_by_id);
CREATE INDEX idx_video_part ON instructional_videos(part_suggestion_id);
CREATE INDEX idx_video_vehicle ON instructional_videos(vehicle_make, vehicle_model);
CREATE INDEX idx_video_category ON instructional_videos(category);
CREATE INDEX idx_video_rating ON instructional_videos(average_rating);
CREATE INDEX idx_video_part_type ON instructional_videos(part_type);

-- Post votes (to track who voted)
CREATE TABLE post_votes (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES forum_posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    vote_type VARCHAR(10) NOT NULL CHECK (vote_type IN ('UP', 'DOWN')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(post_id, user_id)
);

CREATE INDEX idx_post_vote_post ON post_votes(post_id);
CREATE INDEX idx_post_vote_user ON post_votes(user_id);

-- Part suggestion votes
CREATE TABLE part_suggestion_votes (
    id BIGSERIAL PRIMARY KEY,
    suggestion_id BIGINT NOT NULL REFERENCES part_suggestions(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    vote_type VARCHAR(10) NOT NULL CHECK (vote_type IN ('UP', 'DOWN')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(suggestion_id, user_id)
);

CREATE INDEX idx_suggestion_vote_suggestion ON part_suggestion_votes(suggestion_id);
CREATE INDEX idx_suggestion_vote_user ON part_suggestion_votes(user_id);

-- Video ratings
CREATE TABLE video_ratings (
    id BIGSERIAL PRIMARY KEY,
    video_id BIGINT NOT NULL REFERENCES instructional_videos(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(video_id, user_id)
);

CREATE INDEX idx_video_rating_video ON video_ratings(video_id);
CREATE INDEX idx_video_rating_user ON video_ratings(user_id);

-- Insert default forum categories
INSERT INTO forum_categories (name, slug, description, icon_name, display_order) VALUES
('General Discussion', 'general', 'General automotive discussions and chat', 'chat', 1),
('Technical Help', 'technical', 'Ask technical questions and get help from the community', 'wrench', 2),
('Parts & Accessories', 'parts', 'Discuss parts, accessories, and where to find them', 'cog', 3),
('DIY & How-To', 'diy', 'Share and find DIY guides and tutorials', 'book', 4),
('Show & Shine', 'showcase', 'Show off your vehicles and builds', 'star', 5),
('Classifieds', 'classifieds', 'Buy, sell, and trade automotive items', 'tag', 6);

-- Insert demo users
INSERT INTO users (username, email, display_name, bio, is_moderator) VALUES
('admin', 'admin@virtualgarage.com', 'Admin', 'Virtual Garage Administrator', TRUE),
('demo_user', 'demo@virtualgarage.com', 'Demo User', 'A demo community member', FALSE),
('gearhead_mike', 'mike@example.com', 'Gearhead Mike', 'Classic car enthusiast and DIY mechanic', FALSE),
('wrench_wendy', 'wendy@example.com', 'Wrench Wendy', 'Professional mechanic sharing knowledge', FALSE);

-- Insert sample instructional videos (popular YouTube car repair channels)
INSERT INTO instructional_videos (title, description, video_url, thumbnail_url, source, video_id, category, part_type, vehicle_make, vehicle_model, year_start, year_end, difficulty_level, is_verified) VALUES
('How to Replace Brake Pads - Complete Guide', 'Step-by-step guide to replacing front brake pads on most vehicles', 'https://www.youtube.com/watch?v=example1', 'https://img.youtube.com/vi/example1/mqdefault.jpg', 'YOUTUBE', 'example1', 'Brakes', 'Brake Pads', NULL, NULL, NULL, NULL, 'BEGINNER', TRUE),
('Oil Change Tutorial - Do It Yourself', 'Complete oil change walkthrough for beginners', 'https://www.youtube.com/watch?v=example2', 'https://img.youtube.com/vi/example2/mqdefault.jpg', 'YOUTUBE', 'example2', 'Maintenance', 'Oil Filter', NULL, NULL, NULL, NULL, 'BEGINNER', TRUE),
('Air Filter Replacement Guide', 'How to replace your engine air filter in minutes', 'https://www.youtube.com/watch?v=example3', 'https://img.youtube.com/vi/example3/mqdefault.jpg', 'YOUTUBE', 'example3', 'Engine', 'Air Filter', NULL, NULL, NULL, NULL, 'BEGINNER', TRUE),
('Spark Plug Replacement - All You Need to Know', 'Complete guide to changing spark plugs', 'https://www.youtube.com/watch?v=example4', 'https://img.youtube.com/vi/example4/mqdefault.jpg', 'YOUTUBE', 'example4', 'Engine', 'Spark Plugs', NULL, NULL, NULL, NULL, 'INTERMEDIATE', TRUE),
('How to Replace Headlight Bulbs', 'Easy headlight bulb replacement tutorial', 'https://www.youtube.com/watch?v=example5', 'https://img.youtube.com/vi/example5/mqdefault.jpg', 'YOUTUBE', 'example5', 'Electrical', 'Headlight Bulb', NULL, NULL, NULL, NULL, 'BEGINNER', TRUE),
('Battery Replacement and Care', 'How to safely replace your car battery', 'https://www.youtube.com/watch?v=example6', 'https://img.youtube.com/vi/example6/mqdefault.jpg', 'YOUTUBE', 'example6', 'Electrical', 'Battery', NULL, NULL, NULL, NULL, 'BEGINNER', TRUE),
('Coolant Flush Complete Guide', 'How to flush and replace coolant/antifreeze', 'https://www.youtube.com/watch?v=example7', 'https://img.youtube.com/vi/example7/mqdefault.jpg', 'YOUTUBE', 'example7', 'Cooling', 'Coolant', NULL, NULL, NULL, NULL, 'INTERMEDIATE', TRUE),
('Alternator Replacement Tutorial', 'Step-by-step alternator replacement', 'https://www.youtube.com/watch?v=example8', 'https://img.youtube.com/vi/example8/mqdefault.jpg', 'YOUTUBE', 'example8', 'Electrical', 'Alternator', NULL, NULL, NULL, NULL, 'INTERMEDIATE', TRUE),
('Fuel Filter Change Guide', 'How to replace your fuel filter safely', 'https://www.youtube.com/watch?v=example9', 'https://img.youtube.com/vi/example9/mqdefault.jpg', 'YOUTUBE', 'example9', 'Fuel System', 'Fuel Filter', NULL, NULL, NULL, NULL, 'INTERMEDIATE', TRUE),
('Serpentine Belt Replacement', 'Complete guide to replacing the serpentine belt', 'https://www.youtube.com/watch?v=example10', 'https://img.youtube.com/vi/example10/mqdefault.jpg', 'YOUTUBE', 'example10', 'Engine', 'Serpentine Belt', NULL, NULL, NULL, NULL, 'INTERMEDIATE', TRUE);

-- Insert sample part suggestions
INSERT INTO part_suggestions (author_id, part_name, part_number, brand, description, part_category, vehicle_make, vehicle_model, year_start, year_end, price_paid, where_to_buy, installation_difficulty, installation_time_minutes, installation_notes, tools_required, upvote_count, is_verified) VALUES
(3, 'Bosch QuietCast Premium Brake Pads', 'BP1108', 'Bosch', 'Excellent OE-equivalent brake pads with low dust formula', 'Brakes', NULL, NULL, NULL, NULL, 45.99, 'RockAuto, Amazon, AutoZone', 'EASY', 45, 'Make sure to bed in the pads properly after installation', 'Jack, lug wrench, C-clamp, socket set', 15, TRUE),
(4, 'Mobil 1 Extended Performance Oil Filter', 'M1-110A', 'Mobil 1', 'High-capacity oil filter good for up to 20,000 miles', 'Maintenance', NULL, NULL, NULL, NULL, 12.99, 'Walmart, Amazon, AutoZone', 'EASY', 30, 'Pre-fill the filter with oil before installing', 'Oil filter wrench, drain pan', 22, TRUE),
(3, 'NGK Iridium IX Spark Plugs', 'BKR6EIX', 'NGK', 'Premium iridium spark plugs for improved performance and longevity', 'Engine', NULL, NULL, NULL, NULL, 8.99, 'RockAuto, Amazon, Advance Auto Parts', 'MODERATE', 60, 'Use anti-seize on threads, torque to spec', 'Spark plug socket, torque wrench, gap tool', 18, TRUE);

-- Insert sample forum threads
INSERT INTO forum_threads (title, content, category_id, author_id, thread_type, last_activity_at) VALUES
('Best brake pads for daily driving?', 'Looking for recommendations on brake pads that are quiet and low dust. My car is a 2019 Honda Accord. What have you all had good experiences with?', 3, 3, 'QUESTION', NOW()),
('Just finished my first oil change!', 'Finally did my own oil change today. Saved about $40 compared to going to the shop. The ChrisFix video made it super easy!', 4, 4, 'DISCUSSION', NOW()),
('Show off your weekend project', 'Post pictures of what you''re working on this weekend! I''m doing a full brake job on my truck.', 5, 3, 'SHOWCASE', NOW());

-- Insert sample forum posts
INSERT INTO forum_posts (content, thread_id, author_id, upvote_count) VALUES
('I''ve been using Bosch QuietCast on my Accord and they''re great. Very little dust and no squeaking.', 1, 4, 5),
('Second the Bosch recommendation. Also check out the Akebono ProACT if you want ceramic pads.', 1, 2, 3),
('Congrats on the first oil change! It gets easier every time. Pro tip: always check for leaks the next day.', 2, 3, 2);
