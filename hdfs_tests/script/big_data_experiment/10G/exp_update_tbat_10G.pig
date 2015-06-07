fs -rm -r -f /user/fyu/column-store-tbat/data/1G/tbat_updated;
tbat0 = LOAD '/user/fyu/column-store-tbat/data/1G/tbat.txt'  USING PigStorage(',') AS (oid:INT, val:INT);
update01 = LOAD '/user/fyu/column-store-tbat/data/1G/update0.01.txt' USING PigStorage(',') AS (oid:INT, val:INT);

tbat_updated = UNION tbat0, update01;
STORE tbat_updated INTO '/user/fyu/column-store-tbat/data/1G/tbat_updated';






