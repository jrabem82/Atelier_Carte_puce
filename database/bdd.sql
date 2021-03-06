drop table if exists ab_concern;
drop table if exists ab_connect;
drop table if exists ab_log;
drop table if exists ab_session;
drop table if exists ab_user;

create table ab_user(
  idUser varchar(50) not null,
  x_hmac BIGINT not null,
  y_hmac BIGINT not null,
  hmac_x_y_pw varchar(500) not null,
  histoR varchar(1024) not null,
  histoG varchar(1024) not null,
  histoB varchar(1024) not null,
  NBAuthEchouees integer not null,
  constraint pk_user primary key (idUser)
);

create table ab_session(
	idSession varchar(50) not null,
  dateDebutSession timestamp not null,
  dateFinSession timestamp not null,
  constraint pk_session primary key(idSession)
);

create table ab_log(
	idLog varchar(50) not null,
  dateLog timestamp not null,
  textLog varchar(500) not null,
  constraint pk_log primary key(idLog)
);

create table ab_connect(
	idUser varchar(50) not null,
  idSession varchar(50) not null,
  constraint pk_connect primary key (idUser, idSession),
  constraint fk_connect_user foreign key (idUser) references ab_user(idUser),
  constraint fk_connect_session foreign key (idSession) references ab_session(idSession)
);

create table ab_concern(
	idLog varchar(50) not null,
  idSession varchar(50) not null,
  constraint pk_concern primary key (idLog, idSession),
  constraint fk_concern_log foreign key (idLog) references ab_log(idLog),
  constraint fk_concern_session foreign key (idSession) references ab_session(idSession)
);

create or replace function random_string(length integer) returns text as
$$
declare
  chars text[] := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
  result text := '';
  i integer := 0;
begin
  if length < 0 then
    raise exception 'Given length cannot be less than 0';
  end if;
  for i in 1..length loop
    result := result || chars[1+random()*(array_length(chars, 1)-1)];
  end loop;
  return result;
end;
$$ language plpgsql;

create or replace function newUserID() returns varchar as
$$
declare
	randomID text := '';
    nbFound integer := 0;
    alreadyExists boolean := true;
begin
    while alreadyExists loop
    	randomID := random_string(15);
    	select count(idUser) into nbFound from ab_user where idUser = randomID;
        if nbFound = 0 then
        	alreadyExists := false;
        end if;
    end loop;

    return randomID;
end;
$$ language plpgsql;

create or replace function newSessionID() returns varchar as
$$
declare
	randomID text := '';
    nbFound integer := 0;
    alreadyExists boolean := true;
begin
    while alreadyExists loop
    	randomID := random_string(15);
    	select count(idSession) into nbFound from ab_session where idSession = randomID;
        if nbFound = 0 then
        	alreadyExists := false;
        end if;
    end loop;

    return randomID;
end;
$$ language plpgsql;

create or replace function newLogID() returns varchar as
$$
declare
	randomID text := '';
    nbFound integer := 0;
    alreadyExists boolean := true;
begin
    while alreadyExists loop
    	randomID := random_string(15);
    	select count(idLog) into nbFound from ab_log where idLog = randomID;
        if nbFound = 0 then
        	alreadyExists := false;
        end if;
    end loop;

    return randomID;
end;
$$ language plpgsql;

## pw pour hmac = aTuring#95
insert into ab_user values ( 
  'alanTuring',
  744073709551615, 
  844674407370955161, 
  '6F12D429F143FA76685EA3BF60F68D9D', 
  crypt('histoR', gen_salt('md5')), 
  crypt('histoG', gen_salt('md5')), 
  crypt('histoB', gen_salt('md5')),
  2);

## pw pour hmac = aShamir#75
insert into ab_user values ( 
  'adiShamir',
  634073509551718, 
  744654407310925109, 
  '151C8F74F0B02F61FAF6AE1C0009BCFB', 
  crypt('histoR1', gen_salt('md5')), 
  crypt('histoG1', gen_salt('md5')), 
  crypt('histoB1', gen_salt('md5')),
  0);