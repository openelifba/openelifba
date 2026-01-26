-- Index for filtering exercises by category
-- Used in: JooqExerciseMemoryRepository.fetchDueExercises, JooqMemoryRepository.fetchMemoryStatistics
CREATE INDEX idx_exercise_category_id ON exercise (category_id);

-- Composite index for memory lookups by user and category
-- Used in: JooqMemoryRepository.fetchMemoryStatistics, JooqMemoryRepository.fetchDueExerciseCount
CREATE INDEX idx_memory_user_category ON memory (user_id, category_id);

-- Composite index for finding due exercises
-- Used in: JooqExerciseMemoryRepository.fetchDueExercises, JooqMemoryRepository.fetchDueExerciseCount
CREATE INDEX idx_memory_user_category_next_review ON memory (user_id, category_id, next_review_at);

-- Index for joining memory with exercises by exercise_id and user_id
-- Used in: JooqExerciseMemoryRepository.fetchDueExercises
CREATE INDEX idx_memory_exercise_user ON memory (exercise_id, user_id);

-- Index for ordering due exercises by priority (incorrect_count DESC, streak ASC)
-- Used in: JooqExerciseMemoryRepository.fetchDueExercises ORDER BY clause
CREATE INDEX idx_memory_priority ON memory (incorrect_count DESC, streak ASC);
