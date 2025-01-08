-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for aplikasikepegawaian
CREATE DATABASE IF NOT EXISTS `aplikasikepegawaian` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `aplikasikepegawaian`;

-- Dumping structure for table aplikasikepegawaian.absensi
CREATE TABLE IF NOT EXISTS `absensi` (
  `AbsensiID` int NOT NULL AUTO_INCREMENT,
  `KaryawanID` int DEFAULT NULL,
  `Tanggal` date NOT NULL,
  `Status` enum('Hadir','Sakit','Izin','Alpa') NOT NULL,
  PRIMARY KEY (`AbsensiID`),
  KEY `FK_KaryawanID` (`KaryawanID`),
  CONSTRAINT `FK_KaryawanID` FOREIGN KEY (`KaryawanID`) REFERENCES `karyawan` (`KaryawanID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table aplikasikepegawaian.absensi: ~4 rows (approximately)
INSERT INTO `absensi` (`AbsensiID`, `KaryawanID`, `Tanggal`, `Status`) VALUES
	(1, 1, '2025-01-08', 'Alpa');
INSERT INTO `absensi` (`AbsensiID`, `KaryawanID`, `Tanggal`, `Status`) VALUES
	(3, 4, '2025-01-08', 'Izin');
INSERT INTO `absensi` (`AbsensiID`, `KaryawanID`, `Tanggal`, `Status`) VALUES
	(4, 4, '2025-01-08', 'Sakit');
INSERT INTO `absensi` (`AbsensiID`, `KaryawanID`, `Tanggal`, `Status`) VALUES
	(5, 6, '2025-01-08', 'Hadir');

-- Dumping structure for table aplikasikepegawaian.jabatan
CREATE TABLE IF NOT EXISTS `jabatan` (
  `JabatanID` int NOT NULL AUTO_INCREMENT,
  `NamaJabatan` varchar(100) NOT NULL,
  `GajiPokok` decimal(10,2) NOT NULL,
  PRIMARY KEY (`JabatanID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table aplikasikepegawaian.jabatan: ~4 rows (approximately)
INSERT INTO `jabatan` (`JabatanID`, `NamaJabatan`, `GajiPokok`) VALUES
	(1, 'Presidn', 10000.00);
INSERT INTO `jabatan` (`JabatanID`, `NamaJabatan`, `GajiPokok`) VALUES
	(2, 'Pegawai', 12031031.00);
INSERT INTO `jabatan` (`JabatanID`, `NamaJabatan`, `GajiPokok`) VALUES
	(4, 'Mekanik', 123131.00);
INSERT INTO `jabatan` (`JabatanID`, `NamaJabatan`, `GajiPokok`) VALUES
	(5, 'HRD', 12312312.00);

-- Dumping structure for table aplikasikepegawaian.karyawan
CREATE TABLE IF NOT EXISTS `karyawan` (
  `KaryawanID` int NOT NULL AUTO_INCREMENT,
  `Nama` varchar(100) NOT NULL,
  `Alamat` varchar(255) DEFAULT NULL,
  `TanggalLahir` date DEFAULT NULL,
  `JabatanID` int DEFAULT NULL,
  PRIMARY KEY (`KaryawanID`),
  KEY `FK_JabatanID` (`JabatanID`),
  CONSTRAINT `FK_JabatanID` FOREIGN KEY (`JabatanID`) REFERENCES `jabatan` (`JabatanID`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table aplikasikepegawaian.karyawan: ~4 rows (approximately)
INSERT INTO `karyawan` (`KaryawanID`, `Nama`, `Alamat`, `TanggalLahir`, `JabatanID`) VALUES
	(1, 'gno', 'addda12313wdawdasd', '2025-01-10', 2);
INSERT INTO `karyawan` (`KaryawanID`, `Nama`, `Alamat`, `TanggalLahir`, `JabatanID`) VALUES
	(4, 'Agus', 'awdasdw', '2025-01-17', 2);
INSERT INTO `karyawan` (`KaryawanID`, `Nama`, `Alamat`, `TanggalLahir`, `JabatanID`) VALUES
	(5, 'Jamal', 'adadaw', '2025-01-23', 2);
INSERT INTO `karyawan` (`KaryawanID`, `Nama`, `Alamat`, `TanggalLahir`, `JabatanID`) VALUES
	(6, 'Setiawan', 'adasdaw', '2025-01-15', 2);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
