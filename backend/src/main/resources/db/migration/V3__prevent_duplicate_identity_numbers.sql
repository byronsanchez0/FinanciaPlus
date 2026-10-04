CREATE OR REPLACE FUNCTION prevent_duplicate_application_identity()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.identity_number IS NULL THEN
        RETURN NEW;
    END IF;

    PERFORM pg_advisory_xact_lock(
        hashtext(LOWER(NEW.identity_number))
    );

    IF EXISTS (
        SELECT 1
        FROM applications
        WHERE LOWER(identity_number) = LOWER(NEW.identity_number)
          AND id <> NEW.id
    ) THEN
        RAISE EXCEPTION 'Ya existe una solicitud para este documento'
            USING ERRCODE = '23505';
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_prevent_duplicate_application_identity
    ON applications;

CREATE TRIGGER trg_prevent_duplicate_application_identity
BEFORE INSERT OR UPDATE OF identity_number
ON applications
FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_application_identity();
