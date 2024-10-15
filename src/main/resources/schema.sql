-- Create the players table
CREATE TABLE IF NOT EXISTS players (
--    id UUID PRIMARY KEY,  -- UUID primary key for players
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,  -- Unique username field
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    wallet_balance DOUBLE NOT NULL
);

-- Create the bets table
CREATE TABLE IF NOT EXISTS bets (
--    id UUID PRIMARY KEY,  -- UUID primary key for bets
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    player_id UUID NOT NULL,  -- Foreign key referencing players
    bet_amount DOUBLE NOT NULL,
    bet_number INT NOT NULL,  -- The number the player bet on
    random_number INT NOT NULL,  -- The randomly generated number
    result VARCHAR(255) NOT NULL,
    winnings DOUBLE NOT NULL,
    timestamp TIMESTAMP NOT NULL,  -- The timestamp of the bet

    CONSTRAINT fk_bet_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Create the wallet_transactions table
CREATE TABLE IF NOT EXISTS wallet_transactions (
--    id UUID PRIMARY KEY,  -- UUID primary key for wallet transactions
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    player_id UUID NOT NULL,  -- Foreign key referencing players
    transaction_type VARCHAR(255) NOT NULL,  -- Type of transaction ("DEBIT" or "CREDIT")
    amount DOUBLE NOT NULL,
    timestamp TIMESTAMP NOT NULL,

    CONSTRAINT fk_wallet_transaction_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);
