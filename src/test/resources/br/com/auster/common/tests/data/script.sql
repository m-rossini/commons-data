
--
-- POSTGRESQL
-- 


CREATE TABLE ep_invoice_test
(
   invoice_id varchar(10) not null,
   cycle_id int4 not null,
   total_amount numeric(16,4) not null,
   penalt_intr_amount numeric(16,4),
   payment_amount numeric(16,4),
   adjustment_amount numeric(16,4),
   dispute_amount numeric(16,4)
);


CREATE TABLE ep_cycle_test
(
   objid int4 PRIMARY KEY not null,
   cycle_code varchar(5) not null,
   start_date date,
   end_date date
);



--
-- ORACLE
-- 


CREATE TABLE ep_invoice_test
(
   invoice_id varchar2(10) not null,
   cycle_id number(10) not null,
   total_amount number(16,4),
   penalt_intr_amount number(16,4),
   payment_amount number(16,4),
   adjustment_amount number(16,4),
   dispute_amount number(16,4)
);


CREATE TABLE ep_cycle_test
(
   objid number(10) PRIMARY KEY not null,
   cycle_code varchar2(5) not null,
   start_date date,
   end_date date
);

