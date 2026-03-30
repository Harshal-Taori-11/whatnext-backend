-- Add verified column to otp_verification table
ALTER TABLE otp_verification
ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;

-- Update any existing records to have verified = false
UPDATE otp_verification SET verified = FALSE WHERE verified IS NULL;

-- Make the column NOT NULL after setting defaults
ALTER TABLE otp_verification
ALTER COLUMN verified SET NOT NULL;

-- Create index for verified column (optional, helps with queries)
CREATE INDEX IF NOT EXISTS idx_verified ON otp_verification(verified);