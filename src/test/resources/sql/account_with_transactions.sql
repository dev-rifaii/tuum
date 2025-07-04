INSERT INTO account
    (id, customer_id, country_code)
VALUES
    (8123, 212, 'EE');

INSERT INTO balance
    (account_id, amount, currency_code)
VALUES
    (8123, 14.21, 'EUR');

INSERT INTO transaction (account_id, amount, currency_code, direction, description)
VALUES
    (8123, 10.00, 'EUR', 'IN', 'yes'),
    (8123, 10.00, 'EUR', 'OUT', 'no'),
    (8123, 14.00, 'EUR', 'IN', 'yes'),
    (8123, 00.21, 'EUR', 'IN', 'yes');
