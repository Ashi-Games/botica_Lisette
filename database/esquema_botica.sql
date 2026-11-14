CREATE DATABASE  IF NOT EXISTS `db_botica_lisette` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `db_botica_lisette`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: db_botica_lisette
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `detalle_venta`
--

DROP TABLE IF EXISTS `detalle_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_venta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `venta_id` bigint(20) NOT NULL,
  `producto_id` bigint(20) NOT NULL,
  `lote_id` bigint(20) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_det_venta` (`venta_id`),
  KEY `fk_det_producto` (`producto_id`),
  KEY `fk_det_lote` (`lote_id`),
  CONSTRAINT `fk_det_lote` FOREIGN KEY (`lote_id`) REFERENCES `lote` (`id`),
  CONSTRAINT `fk_det_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`),
  CONSTRAINT `fk_det_venta` FOREIGN KEY (`venta_id`) REFERENCES `venta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_venta`
--

LOCK TABLES `detalle_venta` WRITE;
/*!40000 ALTER TABLE `detalle_venta` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalle_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingreso`
--

DROP TABLE IF EXISTS `ingreso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingreso` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proveedor` varchar(120) NOT NULL,
  `tipo_comprobante` varchar(10) NOT NULL,
  `codigo_comprobante` varchar(30) NOT NULL,
  `fecha_comprobante` date NOT NULL,
  `fecha_registro` datetime NOT NULL,
  `usuario_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ingreso_comprobante` (`proveedor`,`tipo_comprobante`,`codigo_comprobante`),
  KEY `fk_ingreso_usuario` (`usuario_id`),
  CONSTRAINT `fk_ingreso_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `ck_ingreso_tipo` CHECK (`tipo_comprobante` in ('BOLETA','FACTURA'))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingreso`
--

LOCK TABLES `ingreso` WRITE;
/*!40000 ALTER TABLE `ingreso` DISABLE KEYS */;
INSERT INTO `ingreso` VALUES (1,'Distribuidora de ejemplo S.A.C.','FACTURA','F001-000123','2026-10-13','2026-11-12 02:55:29',NULL);
/*!40000 ALTER TABLE `ingreso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lote`
--

DROP TABLE IF EXISTS `lote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lote` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `producto_id` bigint(20) NOT NULL,
  `ingreso_id` bigint(20) NOT NULL,
  `codigo_lote` varchar(40) DEFAULT NULL,
  `cantidad` int(11) NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_lote_ingreso` (`ingreso_id`),
  KEY `idx_lote_producto` (`producto_id`),
  KEY `idx_lote_vencimiento` (`fecha_vencimiento`),
  CONSTRAINT `fk_lote_ingreso` FOREIGN KEY (`ingreso_id`) REFERENCES `ingreso` (`id`),
  CONSTRAINT `fk_lote_producto` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`),
  CONSTRAINT `ck_lote_cantidad` CHECK (`cantidad` >= 0)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lote`
--

LOCK TABLES `lote` WRITE;
/*!40000 ALTER TABLE `lote` DISABLE KEYS */;
INSERT INTO `lote` VALUES (1,1,1,'L-001',5,'2027-03-12'),(2,2,1,'L-002',64,'2027-01-21'),(3,3,1,'L-003',18,'2026-11-29'),(4,4,1,'L-004',42,'2026-11-26'),(5,5,1,'L-005',9,'2027-05-11'),(6,6,1,'L-006',55,'2026-12-09'),(7,7,1,'L-007',7,'2027-02-15'),(8,8,1,'L-008',33,'2026-12-06'),(9,9,1,'L-009',27,'2028-01-31'),(10,10,1,'L-010',3,'2026-11-21'),(11,11,1,'L-011',40,'2027-08-04'),(12,12,1,'L-012',12,'2028-12-31');
/*!40000 ALTER TABLE `lote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(120) NOT NULL,
  `categoria` varchar(60) NOT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `stock_minimo` int(11) NOT NULL DEFAULT 0,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Paracetamol 500 mg','Analgésicos',0.50,20,1),(2,'Ibuprofeno 400 mg','Analgésicos',0.80,30,1),(3,'Amoxicilina 500 mg','Antibióticos',1.20,25,1),(4,'Loratadina 10 mg','Antialérgicos',0.70,20,1),(5,'Omeprazol 20 mg','Gastrointestinal',0.90,15,1),(6,'Suero oral sobre','Hidratación',2.50,20,1),(7,'Diclofenaco gel 30 g','Dermatológicos',6.50,10,1),(8,'Vitamina C 1 g','Vitaminas',1.50,15,1),(9,'Alcohol 70° 250 ml','Insumos',5.00,12,1),(10,'Jarabe para la tos 120 ml','Respiratorio',8.50,8,1),(11,'Mascarilla KN95','Insumos',1.50,15,1),(12,'Termómetro digital','Equipos',18.00,5,1);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `nombre_completo` varchar(100) NOT NULL,
  `rol` varchar(20) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `venta`
--

DROP TABLE IF EXISTS `venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha_hora` datetime NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `metodo_pago` varchar(20) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_venta_usuario` (`usuario_id`),
  CONSTRAINT `fk_venta_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venta`
--

LOCK TABLES `venta` WRITE;
/*!40000 ALTER TABLE `venta` DISABLE KEYS */;
/*!40000 ALTER TABLE `venta` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-14  0:41:29
