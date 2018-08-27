DROP TABLE IF EXISTS postgis_reference CASCADE;
CREATE TABLE postgis_reference (
	epsg_4326 geometry(Point,4326),
	epsg_2154 geometry(Point,2154),
	epsg_32620 geometry(Point,32620),
	epsg_2972 geometry(Point,2972),
	epsg_2975 geometry(Point,2975),
	epsg_4471 geometry(Point,4471)
);

-- RGF93 / Lambert-93
INSERT INTO postgis_reference (epsg_4326,epsg_2154)
	SELECT 
		ST_Transform(t.epsg_2154,4326) as epsg_4326, 
		t.epsg_2154 
	FROM (
		SELECT (ST_DumpPoints(
			ST_GeneratePoints(
				ST_SetSRID( 
					'POLYGON((60000 6010000,60000 7130000,1270000 7130000,1270000 6010000,60000 6010000))'::geometry, 
					2154 
				),
				20
			)
		)).geom as epsg_2154
	) t;

-- WGS 84 / UTM zone 20N
INSERT INTO postgis_reference (epsg_4326,epsg_32620)
	SELECT 
		ST_Transform(t.epsg_32620,4326) as epsg_4326, 
		t.epsg_32620
	FROM (
		SELECT (ST_DumpPoints(
			ST_GeneratePoints(
				ST_SetSRID( 
					'MULTIPOLYGON(((680000 1580000,680000 1660000,750000 1660000,750000 1580000,680000 1580000)),((470000 1730000,470000 2020000,730000 2020000,730000 1730000,470000 1730000)))'::geometry,
					 32620 
				),
				20
			)
		)).geom as epsg_32620
	) t;


-- RGFG95 / UTM zone 22N
INSERT INTO postgis_reference (epsg_4326,epsg_2972)
	SELECT 
		ST_Transform(t.epsg_2972,4326) as epsg_4326, 
		t.epsg_2972
	FROM (
		SELECT (ST_DumpPoints(
			ST_GeneratePoints(
				ST_SetSRID( 
					'POLYGON((-100000 700000,500000 700000,500000 100000,-100000 100000,-100000 700000))'::geometry,
					 2972 
				),
				20
			)
		)).geom as epsg_2972
	) t;

-- RGR92 / UTM zone 40S
INSERT INTO postgis_reference (epsg_4326,epsg_2975)
	SELECT 
		ST_Transform(t.epsg_2975,4326) as epsg_4326, 
		t.epsg_2975
	FROM (
		SELECT (ST_DumpPoints(
			ST_GeneratePoints(
				ST_SetSRID( 
					'POLYGON((300000 7620000,300000 7710000,390000 7710000,390000 7620000,300000 7620000))'::geometry,
					 2975 
				),
				20
			)
		)).geom as epsg_2975
	) t;

-- RGM04 / UTM zone 38S
INSERT INTO postgis_reference (epsg_4326,epsg_4471)
	SELECT 
		ST_Transform(t.epsg_4471,4326) as epsg_4326, 
		t.epsg_4471
	FROM (
		SELECT (ST_DumpPoints(
			ST_GeneratePoints(
				ST_SetSRID( 
					'POLYGON((490000 8550000,490000 8620000,540000 8620000,540000 8550000,490000 8550000))'::geometry,
					 4471 
				),
				20
			)
		)).geom as epsg_4471
	) t;

CREATE VIEW postgis_reference_export AS SELECT 
		ST_AsText(ST_SnapToGrid(epsg_4326,1.0e-9))  as epsg_4326,
		ST_AsText(ST_SnapToGrid(epsg_2154,1.0e-5))  as epsg_2154,
		ST_AsText(ST_SnapToGrid(epsg_32620,1.0e-5)) as epsg_32620,
		ST_AsText(ST_SnapToGrid(epsg_2972,1.0e-5))  as epsg_2972,
		ST_AsText(ST_SnapToGrid(epsg_2975,1.0e-5))  as epsg_2975,
		ST_AsText(ST_SnapToGrid(epsg_4471,1.0e-5))  as epsg_4471
	FROM postgis_reference ;



\COPY (SELECT * FROM postgis_reference_export) TO 'reference_postgis.csv' WITH CSV HEADER;

