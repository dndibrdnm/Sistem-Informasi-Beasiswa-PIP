-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 09, 2026 at 05:06 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_beasiswa`
--

-- --------------------------------------------------------

--
-- Table structure for table `siswa`
--

CREATE TABLE `siswa` (
  `nisn` varchar(20) NOT NULL,
  `nama` varchar(100) DEFAULT NULL,
  `kelas` varchar(20) DEFAULT NULL,
  `jurusan` varchar(50) DEFAULT NULL,
  `status_berkas` varchar(50) DEFAULT NULL,
  `status_acc` varchar(50) DEFAULT NULL,
  `keterangan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `siswa`
--

INSERT INTO `siswa` (`nisn`, `nama`, `kelas`, `jurusan`, `status_berkas`, `status_acc`, `keterangan`) VALUES
('222310076', 'dandi', '12', 'OTOMOTIF', 'Lengkap', 'Disetujui', 'Berkas Komplit'),
('89100150', 'FIKA WIDIYA ASRI', 'XI', 'DKV', 'Belum Lengkap', 'Pending', NULL),
('89102735', 'NOVA RAMDINI', 'XI', 'DKV', 'Belum Lengkap', 'Pending', NULL),
('89124301', 'BAGAS', 'XI', 'TITL', 'Lengkap', 'Disetujui', 'Berkas Komplit'),
('89136508', 'SULIS SETIAWATI', 'XI', 'DPIB', 'Belum Lengkap', 'Pending', NULL),
('89167174', 'VIO AMELIA', 'XI', 'TAV', 'Belum Lengkap', 'Pending', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL,
  `nisn` varchar(20) NOT NULL,
  `nama_siswa` varchar(100) DEFAULT NULL,
  `tgl_acc` date DEFAULT NULL,
  `tgl_cair` date DEFAULT NULL,
  `nominal` varchar(50) DEFAULT NULL,
  `keterangan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id_transaksi`, `nisn`, `nama_siswa`, `tgl_acc`, `tgl_cair`, `nominal`, `keterangan`) VALUES
(4, '222310076', 'dandi', '2026-05-07', '2026-05-07', '180000', 'Pencairan Beasiswa Tahap 1');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `siswa`
--
ALTER TABLE `siswa`
  ADD PRIMARY KEY (`nisn`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `nisn` (`nisn`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`nisn`) REFERENCES `siswa` (`nisn`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
