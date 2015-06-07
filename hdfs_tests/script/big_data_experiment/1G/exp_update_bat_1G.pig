bat0 = LOAD '/user/fyu/column-store-tbat/data/1G/bat.txt'  USING PigStorage(',') AS (oid:INT, val:INT);
update01 = LOAD '/user/fyu/column-store-tbat/data/1G/update0.01.txt' USING PigStorage(',') AS (oid:INT, val:INT);


tmp1 = JOIN bat0 BY oid LEFT OUTER, update01 BY oid;
bat_updated = FOREACH tmp1 GENERATE $0, ($2 IS NULL ? $1 : $3);
STORE bat_updated INTO '/user/fyu/column-store-tbat/data/1G/bat_updated';

fs -rm -r -f /user/fyu/column-store-tbat/data/1G/bat_updated;






