-- Run once before deploying the bytea entity mapping to a database with an OID column.
DO $$
DECLARE
    column_type text;
BEGIN
    SELECT data_type
    INTO column_type
    FROM information_schema.columns
    WHERE table_schema = current_schema()
      AND table_name = 'users'
      AND column_name = 'profile_image_data';

    IF column_type = 'oid' THEN
        ALTER TABLE users
            ALTER COLUMN profile_image_data TYPE bytea
            USING CASE
                WHEN profile_image_data IS NULL THEN NULL
                ELSE lo_get(profile_image_data)
            END;
    ELSIF column_type = 'bytea' THEN
        RAISE NOTICE 'users.profile_image_data is already bytea; no conversion needed';
    ELSE
        RAISE EXCEPTION 'Expected users.profile_image_data to be oid or bytea, found %', column_type;
    END IF;
END
$$;
