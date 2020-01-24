/*! SET storage_engine=INNODB */;

create table testpayment_hpp_requests (
  record_id serial
, kb_account_id char(36) not null
, kb_payment_id char(36) default null
, kb_payment_transaction_id char(36) default null
, session_id varchar(255) not null
, additional_data longtext default null
, created_date datetime not null
, kb_tenant_id char(36) not null
, primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create index testpayment_hpp_requests_kb_account_id on testpayment_hpp_requests(kb_account_id);
create unique index testpayment_hpp_requests_kb_session_id on testpayment_hpp_requests(session_id);
create index testpayment_hpp_requests_kb_payment_transaction_id on testpayment_hpp_requests(kb_payment_transaction_id);

create table testpayment_responses (
  record_id serial
, status char(10)not null
, kb_account_id char(36) not null
, kb_payment_id char(36) not null
, kb_payment_transaction_id char(36) not null
, transaction_type varchar(32) not null
, amount numeric(15,9)
, currency char(3)
, additional_data longtext default null
, created_date datetime not null
, kb_tenant_id char(36) not null
, primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create index testpayment_responses_kb_payment_id on testpayment_responses(kb_payment_id);
create index testpayment_responses_kb_payment_transaction_id on testpayment_responses(kb_payment_transaction_id);

create table testpayment_payment_methods (
  record_id serial
, kb_account_id char(36) not null
, kb_payment_method_id char(36) not null
, is_deleted smallint not null default 0
, additional_data longtext default null
, created_date datetime not null
, updated_date datetime not null
, kb_tenant_id char(36) not null
, primary key(record_id)
) /*! CHARACTER SET utf8 COLLATE utf8_bin */;
create unique index testpayment_payment_methods_kb_payment_id on testpayment_payment_methods(kb_payment_method_id);
