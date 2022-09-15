-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema fcs
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema fcs
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `fcs` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `fcs` ;

-- -----------------------------------------------------
-- Table `fcs`.`_auth`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fcs`.`_auth` (
  `username` VARCHAR(50) NOT NULL,
  `uid` VARCHAR(36) NOT NULL,
  `pass_hash` TEXT NOT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE INDEX `username` (`username` ASC) VISIBLE,
  UNIQUE INDEX `uid` (`uid` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `fcs`.`_order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fcs`.`_order` (
  `order_id` INT NOT NULL AUTO_INCREMENT,
  `buyer_uid` VARCHAR(36) NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `paid` TINYINT(1) NULL DEFAULT '0',
  `amount` INT NOT NULL,
  `_status` TINYTEXT NOT NULL,
  `_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  UNIQUE INDEX `order_id` (`order_id` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `fcs`.`_transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fcs`.`_transaction` (
  `id` VARCHAR(100) NOT NULL,
  `uid` VARCHAR(36) NOT NULL,
  `approved` TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `fcs`.`_user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fcs`.`_user` (
  `uid` VARCHAR(36) NOT NULL,
  `_type` TINYTEXT NOT NULL,
  `username` VARCHAR(25) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `first_name` VARCHAR(25) NOT NULL,
  `last_name` VARCHAR(25) NULL DEFAULT NULL,
  PRIMARY KEY (`username`),
  UNIQUE INDEX `uid` (`uid` ASC) VISIBLE,
  UNIQUE INDEX `email` (`email` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `fcs`.`cart`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fcs`.`cart` (
  `uid` VARCHAR(36) NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT '1',
  PRIMARY KEY (`uid`, `product_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `fcs`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fcs`.`product` (
  `product_id` INT NOT NULL AUTO_INCREMENT,
  `category` VARCHAR(50) NULL DEFAULT NULL,
  `_name` VARCHAR(50) NULL DEFAULT NULL,
  `_inventory` INT NOT NULL DEFAULT '0',
  `catalog` LONGBLOB NOT NULL,
  `approved` TINYINT(1) NOT NULL DEFAULT '0',
  `image_1` LONGBLOB NOT NULL,
  `image_2` LONGBLOB NOT NULL,
  `details` TEXT NULL DEFAULT NULL,
  `seller_username` VARCHAR(25) NOT NULL,
  `price` INT NOT NULL,
  PRIMARY KEY (`product_id`),
  UNIQUE INDEX `product_id` (`product_id` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `fcs`.`wallet`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fcs`.`wallet` (
  `uid` VARCHAR(36) NOT NULL,
  `balance` INT NOT NULL DEFAULT '0',
  `hold` INT NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `fcs`.`withdrawal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `fcs`.`withdrawal` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `uid` VARCHAR(36) NOT NULL,
  `amount` INT NOT NULL,
  `_status` TINYTEXT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
