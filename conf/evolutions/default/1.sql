# --- !Ups

SET SCHEMA 'health-keeper';

CREATE TABLE users (
  uuid        UUID PRIMARY KEY            NOT NULL,
  create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  update_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  first_name  CHARACTER VARYING(255)      NOT NULL,
  last_name   CHARACTER VARYING(255)      NOT NULL,
  email       CHARACTER VARYING(255)      NOT NULL,
  status      INTEGER                     NOT NULL,
  deleted     BOOLEAN                     NOT NULL,
  salt        CHARACTER VARYING(255)      NOT NULL,
  password    CHARACTER VARYING(255)      NOT NULL,
  role        INTEGER                     NOT NULL
);
CREATE UNIQUE INDEX uk_users_email ON users USING BTREE (email);

CREATE TABLE medical_cards (
  uuid        UUID PRIMARY KEY            NOT NULL,
  create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  update_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  user_id     UUID                        NOT NULL UNIQUE,
  birth_date  DATE                        NOT NULL,
  weight      DECIMAL                     NOT NULL,
  height      DECIMAL                     NOT NULL
);
ALTER TABLE medical_cards ADD FOREIGN KEY (user_id) REFERENCES users (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE TABLE medical_pairs (
  uuid        UUID PRIMARY KEY            NOT NULL,
  create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  update_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  patient_id  UUID                        NOT NULL,
  doctor_id   UUID                        NOT NULL
);
ALTER TABLE medical_pairs ADD FOREIGN KEY (patient_id) REFERENCES users (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE medical_pairs ADD FOREIGN KEY (doctor_id) REFERENCES users (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
CREATE UNIQUE INDEX uk_medical_pairs_unqie_pair ON medical_pairs (patient_id, doctor_id);

CREATE TABLE medical_pair_requests (
  uuid           UUID PRIMARY KEY            NOT NULL,
  create_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  update_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  patient_id     UUID                        NOT NULL,
  doctor_id      UUID                        NOT NULL,
  message        VARCHAR(255),
  reject_message VARCHAR(255),
  status         INT                         NOT NULL
);
ALTER TABLE medical_pair_requests ADD FOREIGN KEY (patient_id) REFERENCES users (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE medical_pair_requests ADD FOREIGN KEY (doctor_id) REFERENCES users (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE TABLE devices (
  uuid        UUID PRIMARY KEY            NOT NULL,
  create_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  update_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  user_id     UUID                        NOT NULL,
  name        VARCHAR(255)                NOT NULL,
  device_type INT                         NOT NULL
);
ALTER TABLE devices ADD FOREIGN KEY (user_id) REFERENCES users (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE TABLE device_measurements (
  uuid           UUID PRIMARY KEY            NOT NULL,
  create_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  update_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  device_id      UUID                        NOT NULL,
  value          DECIMAL                     NOT NULL,
  timestamp      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  unit           INT                         NOT NULL,
  measurement_id UUID                        NOT NULL
);
ALTER TABLE device_measurements ADD FOREIGN KEY (device_id) REFERENCES devices (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE TABLE weekly_results (
  uuid           UUID PRIMARY KEY            NOT NULL,
  create_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  average_result DECIMAL                     NOT NULL,
  t_student      DECIMAL                     NOT NULL,
  deviation      DECIMAL                     NOT NULL
);

CREATE TABLE weekly_measurements (
  uuid                  UUID PRIMARY KEY            NOT NULL,
  device_measurement_id UUID                        NOT NULL,
  weekly_result_id      UUID                        NOT NULL,
  week_start_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  FOREIGN KEY (device_measurement_id) REFERENCES device_measurements (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (weekly_result_id) REFERENCES weekly_results (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE measurements_similarities (
  uuid                         UUID PRIMARY KEY NOT NULL,
  first_weekly_measurement_id  UUID             NOT NULL,
  second_weekly_measurement_id UUID             NOT NULL,
  correlation_coefficient      DECIMAL          NOT NULL,
  FOREIGN KEY (first_weekly_measurement_id) REFERENCES weekly_measurements (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  FOREIGN KEY (second_weekly_measurement_id) REFERENCES weekly_measurements (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE fitbit_integrations (
  uuid           UUID PRIMARY KEY            NOT NULL,
  create_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  update_date    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  user_id        UUID                        NOT NULL,
  device_id      UUID                        NOT NULL,
  fitbit_user_id CHARACTER VARYING(255)      NOT NULL,
  code           CHARACTER VARYING(255)      NOT NULL,
  access_token   CHARACTER VARYING(255)      NOT NULL,
  refresh_token  CHARACTER VARYING(255)      NOT NULL
);
ALTER TABLE fitbit_integrations ADD FOREIGN KEY (user_id) REFERENCES users (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE fitbit_integrations ADD FOREIGN KEY (device_id) REFERENCES devices (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
CREATE UNIQUE INDEX uk_fitbit_integrations_user_id_fitbit_user_id ON fitbit_integrations (user_id, fitbit_user_id);

INSERT INTO "health-keeper"."users" ("uuid", "create_date", "update_date", "first_name", "last_name",
                                     "email", "status", "deleted", "salt", "password", "role")
VALUES ('8eccaf86-36a7-47c6-a5a5-5c7a7650e1a4', now(), now(), 'Max', 'Ochenashko', 'test@test.com', 0, FALSE,
                                                '$2a$10$M/4CcqzTRV9Kaln8YOuGDu',
                                                '$2a$10$M/4CcqzTRV9Kaln8YOuGDuOvso45qmzmwfVevFCanfyHms6cSm9IK', 0);


# --- !Downs

DROP TABLE fitbit_integrations CASCADE;
DROP TABLE devices CASCADE;
DROP TABLE medical_pairs CASCADE;
DROP TABLE medical_pair_requests CASCADE;
DROP TABLE medical_cards CASCADE;
DROP TABLE users CASCADE;