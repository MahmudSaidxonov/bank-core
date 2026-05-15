CREATE INDEX IF NOT EXISTS idx_transaction_from_account ON transactions(from_account_id);
CREATE INDEX IF NOT EXISTS idx_transaction_to_account ON transactions(to_account_id);
CREATE INDEX IF NOT EXISTS idx_transaction_created_at ON transactions(created_at);
CREATE INDEX IF NOT EXISTS idx_account_user_id ON accounts(user_id);