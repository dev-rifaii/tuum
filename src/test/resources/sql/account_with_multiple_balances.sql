INSERT INTO account
(id, customer_id, country_code)
VALUES
    (51, 413, 'US');

INSERT INTO balance
(account_id, amount, currency_code)
VALUES
    (51, 0.00, 'SEK'),
    (51, 0.00, 'USD');
