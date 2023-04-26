CREATE TABLE customers (
	name varchar(150) NOT NULL,
    personalIDNo varchar(100) NOT NULL PRIMARY KEY,
    phoneNumber varchar(100) NOT NULL
    );
    
	INSERT INTO customers VALUES ('Jane Doe', '140789-12548', '+7 (789) 654-7890');
    INSERT INTO customers VALUES ('John Doe', '051273-10156', '+8 (741) 025-9630');
    INSERT INTO customers VALUES ('Stacey Bloom', '100963-21785', '+ 5 (214) 789-6301');
    INSERT INTO customers VALUES('Michael Patel', '260589-15687', '+1 (234) 567-8901'); 
    INSERT INTO customers VALUES('Ryan Lee', '310885-19874', '+1 (345) 678-9012'); 
    INSERT INTO customers VALUES('William Jackson', '020693-10238', '+1 (456) 789-0123'); 
    INSERT INTO customers VALUES('Benjamin Kim', '161170-10045', '+1 (567) 890-1235');
    INSERT INTO customers VALUES('Andris Balodis', '230173-11345', '+371 25395681');
	INSERT INTO customers VALUES('Māris Roze', '111191-10233', '+371 27401983');
	INSERT INTO customers VALUES('Gatis Liepa', '011265-12753', '+370 24960492');
    INSERT INTO customers VALUES('Taylor Moore', '111207-59887', '+1 (375) 569-8952'); 
    INSERT INTO customers VALUES('Maggie Smith', '050967-36687', '+1 (493) 120-8952'); 
    INSERT INTO customers VALUES('John Addams', '120569-10002', '+1 (437) 890-5523');
    
    SELECT * FROM customers;
       
CREATE TABLE tools (
    category VARCHAR(50) NOT NULL,
    id VARCHAR(45) NOT NULL PRIMARY KEY,
    name TEXT(100) NOT NULL,
    specifications TEXT(250) NOT NULL,
    serviceHours INT,
    priceDay VARCHAR(25) NOT NULL
);
 	
    INSERT INTO tools VALUES ('Saws', 'SAW100', 'Electric chainsaw HUSQVARNA 418EL', 'Battery-powered.
    Weight: 7,5 kg. Power: 1,8 kW. Capacity: 15 m/s.', 500, '25.00 EUR');
    INSERT INTO tools VALUES ('Saws', 'SAW200', 'Circular saw MAKITA GSH03Z', 'Cordless. Voltage: max. 40 V. 
    Power: 4000 RPM. Weight: 5,5 kg. Blade diam.: 190 mm. Max cutting capacity: 68,5 mm.', 500, '25.00 EUR');
    INSERT INTO tools VALUES ('Saws', 'SAW300', 'Industrial table saw Grizzly G1023RLA40', 'Blade size: 35 cm. 
    Weight: 264 kg. Table dimensions: 123 x 69 cm. Motor: 5 HP', 500, '150.00 EUR');
    INSERT INTO tools VALUES('Drills', 'DRILL1', 'Drilling machine', 'Max diameter 13 mm, Weight 2.5 kg, 
	Power 18 V', '500', '11.50 EUR'); 
    INSERT INTO tools VALUES('Drills', 'DRILL2', 'Diamond drill', 'Max diameter 162 mm, Weight 8.2 kg, 
    Power 2.2 kW', '500', '39.00 EUR'); 
    INSERT INTO tools VALUES('Drills', 'DRILL3', 'Rotary hammer', 'Max diameter 25 mm, Weight 2.9 kg, 
    Power 650 W', '500', '11.50 EUR');
    INSERT INTO tools VALUES('Sanders and grinders', 'SL01', 'Bosch Orbit Sander ', 
    'size 81 x 133, rpm 5000, weight 3.5 kg ', '250', '15.25 EUR');
    INSERT INTO tools VALUES('Sanders and grinders', 'SL02', 'Makita Finishing Sander', 
    'size 75 x 83, rpm 7000, weight 4.4 kg', '250', '19.15 EUR');
    INSERT INTO tools VALUES('Sanders and grinders', 'SL03', 'Cat SHEET SANDER ', 
    'size 89 x 123, rpm 10000, weight 3.8 kg ', '250', '8.50 EUR');
    INSERT INTO tools VALUES('Meters and canners', 'IRCAM1', 'Infrared camera', 
    'Power: 12V 2.0Ah, size: 102 x 115 x 231 mm, precision: +/- 2,0°, minimum distance: 3.0 m', 500, '40.00 EUR');
    INSERT INTO tools VALUES('Meters and scanners','SCAM1', 'Sewer Camera', 'Power: 12V 2.0Ah, diameter: 12.4 mm, 
    weight: 0.7 kg, display size: 85 mm', 500, '25.00 EUR');
	INSERT INTO tools VALUES('Meters and scanners', 'WSCAN1', 'METAL WALL SCANNER', 'Power: 4x1.5V, size: 220 x 97 x 120 mm, 
    precision: +/- 5 mm, maximum depth: 150 mm', 500, '15.00 EUR');
    
SELECT * FROM tools; 

CREATE TABLE main (
	nPk int PRIMARY KEY NOT NULL AUTO_INCREMENT,
    toolID varchar(45) NOT NULL REFERENCES tools(id),
    toolName varchar(100) NOT NULL REFERENCES tools(name),
    rented varchar(100) REFERENCES customers(name),
    rentedPIN varchar(45) REFERENCES customers(peronalIDNo),
    contacts varchar(100) REFERENCES customers(phoneNumber),
    available int DEFAULT 1,
    untilService int NOT NULL
    );

DROP TABLE main;

SELECT * FROM main;
      

   


        
        