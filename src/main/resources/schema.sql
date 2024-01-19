DROP TABLE IF EXISTS users, requests, items, booking, comments CASCADE;

CREATE TABLE users
(
    user_id  INT4         generated by default as identity primary key,
    email    VARCHAR(100) NOT NULL
        CONSTRAINT "email_unique" UNIQUE,
    name     VARCHAR(100) NOT NULL
);

CREATE TABLE requests
(
    request_id   INT4         generated by default as identity primary key,
    description  VARCHAR(200) NOT NULL,
--    created      TIMESTAMP    NOT NULL,
    requestor_id INT4         NOT NULL,
    CONSTRAINT request_user_fkey
        FOREIGN KEY (requestor_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE
);

CREATE TABLE items
(
    item_id		    INT4         	generated by default as identity primary key,
    name		    VARCHAR(100)	NOT NULL,
    description	    VARCHAR(200)	NOT NULL,
    is_available    BOOL 		    NOT NULL DEFAULT FALSE,
    owner_id	    INT4        	NOT NULL,
    request_id      INT4            ,
    CONSTRAINT item_request_fkey
        FOREIGN KEY (request_id)
            REFERENCES requests (request_id)
            ON DELETE CASCADE,
    CONSTRAINT item_user_fkey
        FOREIGN KEY (owner_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE
);

CREATE TABLE booking
(
    booking_id		INT4		generated by default as identity primary key,
    booking_start	TIMESTAMP WITHOUT TIME ZONE    NOT NULL,
    booking_end		TIMESTAMP WITHOUT TIME ZONE    NOT NULL,
    item_id	        INT4                           NOT NULL,
    user_id	        INT4                           NOT NULL,
    status 			VARCHAR(20) CHECK (status in ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED')),
    CONSTRAINT check_booking_data
        CHECK (booking_end > booking_start),
    CONSTRAINT booking_item_user_fkey
        FOREIGN KEY (item_id)
            REFERENCES items (item_id)
            ON delete CASCADE,
    CONSTRAINT booking_user_item_fkey
        FOREIGN KEY (user_id)
            REFERENCES users (user_id)
            ON delete CASCADE
);

CREATE TABLE comments
(
    comments_id   INT4        generated by default as identity primary key,
    text          TEXT        NOT NULL,
    item_id       INT4        NOT NULL,
    author_id     INT4        NOT NULL,
    created       TIMESTAMP WITHOUT TIME ZONE    NOT NULL,
    CONSTRAINT comment_item_fkey
        FOREIGN KEY (item_id)
            REFERENCES items (item_id)
            ON DELETE CASCADE,
    CONSTRAINT comment_author_fkey
        FOREIGN KEY (author_id)
            REFERENCES users (user_id)
            ON DELETE CASCADE
);