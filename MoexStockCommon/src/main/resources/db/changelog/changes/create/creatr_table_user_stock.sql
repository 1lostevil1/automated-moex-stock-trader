CREATE TABLE user_stock(
	id TEXT REFERENCES tg_user(id),
	ticker VARCHAR(20) REFERENCES stock(ticker),
	PRIMARY KEY(id,ticker)
)