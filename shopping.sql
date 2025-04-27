-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : dim. 27 avr. 2025 à 19:12
-- Version du serveur : 8.0.41
-- Version de PHP : 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `shopping`
--

-- --------------------------------------------------------

--
-- Structure de la table `avis`
--

DROP TABLE IF EXISTS `avis`;
CREATE TABLE IF NOT EXISTS `avis` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titre` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `note` int NOT NULL,
  `description` varchar(10000) COLLATE utf8mb4_general_ci NOT NULL,
  `produit_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_avis_produit` (`produit_id`),
  KEY `fk_avis_user` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `avis`
--

INSERT INTO `avis` (`id`, `titre`, `note`, `description`, `produit_id`, `user_id`) VALUES
(1, 'Bien', 5, 'Bon produit', 1, 87),
(2, 'Sympathique', 4, 'Une valeur sûre', 7, 87),
(7, 'tres bien', 9, 'top', 2, 87),
(9, 'bien', 8, 'top ', 2, 87),
(10, 'super', 5, 'top', 3, 87),
(11, 'c bien', 4, 'top', 2, 87),
(13, 'trop topp', 4, 'super produit', 10, 87),
(15, 'Pas mal', 3, 'Cher pour la qualité', 1, 0);

-- --------------------------------------------------------

--
-- Structure de la table `element_panier`
--

DROP TABLE IF EXISTS `element_panier`;
CREATE TABLE IF NOT EXISTS `element_panier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quantite` int NOT NULL,
  `produit_id` int NOT NULL,
  `panier_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_element_panier` (`panier_id`),
  KEY `fk_produit_panier` (`produit_id`)
) ENGINE=MyISAM AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `element_panier`
--

INSERT INTO `element_panier` (`id`, `quantite`, `produit_id`, `panier_id`) VALUES
(118, 1, 5, 41),
(117, 1, 6, 41),
(119, 1, 2, 42);

-- --------------------------------------------------------

--
-- Structure de la table `marque`
--

DROP TABLE IF EXISTS `marque`;
CREATE TABLE IF NOT EXISTS `marque` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `image` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(10000) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `marque`
--

INSERT INTO `marque` (`id`, `nom`, `image`, `description`) VALUES
(1, 'Apple', 'apple.png', 'Plus grande marque de produits électroniques des USA'),
(2, 'Samsung', 'samsung.png', 'Marque coréenne'),
(3, 'Sony', 'sony.png', 'Marque connue pour sa license PlayStation mais aussi ses produits de son'),
(4, 'Nintendo', 'nintendo.png', 'Marque japonaise du jeu vidéo'),
(5, 'Xiaomi', 'xiaomi.png', 'Entreprise Chinoise de produits électroniques');

-- --------------------------------------------------------

--
-- Structure de la table `panier`
--

DROP TABLE IF EXISTS `panier`;
CREATE TABLE IF NOT EXISTS `panier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `utilisateur_id` int NOT NULL,
  `taille` int NOT NULL,
  `etat` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_users` (`utilisateur_id`)
) ENGINE=MyISAM AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `panier`
--

INSERT INTO `panier` (`id`, `utilisateur_id`, `taille`, `etat`) VALUES
(42, 89, 1, 0),
(41, 89, 0, 1);

-- --------------------------------------------------------

--
-- Structure de la table `produit`
--

DROP TABLE IF EXISTS `produit`;
CREATE TABLE IF NOT EXISTS `produit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `image` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `marque_id` int NOT NULL,
  `prix` double NOT NULL,
  `quantite` int NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `category` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_marque_produit` (`marque_id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `produit`
--

INSERT INTO `produit` (`id`, `nom`, `image`, `marque_id`, `prix`, `quantite`, `description`, `category`) VALUES
(1, 'Iphone', 'iphone.png', 1, 1984, 4, 'tel taxé', 'telephones'),
(2, 'Samsung', 'telsamsung.png', 2, 500, 5, 'android', 'telephones'),
(4, 'Macbook Air M1', 'macbook.png', 1, 1200, 10, 'Ordinateur portable léger', 'ordinateurs'),
(5, 'Xiaomi Redmi Note 12', 'redmi.png', 5, 300, 50, 'Smartphone milieu de gamme', 'telephones'),
(6, 'Nintendo Switch', 'switch.png', 4, 350, 20, 'Console de jeu portable', 'consoles'),
(7, 'TV Samsung 50’’ 4K', 'tv.png', 2, 600, 5, 'Télévision Smart 50 pouces', 'autres'),
(8, 'Casque Sony WH1000XM4', 'casque.png', 3, 400, 29, 'Casque réduction de bruit', 'accessoires'),
(9, 'Trottinette Xiaomi Pro', 'trot.png', 5, 500, 20, 'Trottinette électrique', 'autres'),
(10, 'PlayStation 5', 'ps5.png', 3, 550, 25, 'Console de nouvelle génération', 'consoles'),
(11, 'Samsung Galaxy Tab S9 FE Tablette', 'tabletteSamsung.png', 2, 500, 200, 'Tablette Samsung', 'autres'),
(12, 'Enceinte Sony SRS-XB100', 'enceinteSony.png', 3, 50, 100, 'Enceinte pas cher mais très efficace', 'accessoires'),
(13, 'Ordinateur Samsung Galaxy Book4 ', 'pcSamsung.png', 2, 900, 100, 'Ordinateur dernière génération', 'ordinateurs'),
(14, 'Nintendo Switch Lite Console', 'switchLite.png', 4, 200, 2000, 'Petite console parfait pour les déplacements', 'consoles'),
(15, 'Apple Watch SE', 'appleWatch.png', 1, 300, 300, 'Montre connectée Apple', 'accessoires');

-- --------------------------------------------------------

--
-- Structure de la table `reduction`
--

DROP TABLE IF EXISTS `reduction`;
CREATE TABLE IF NOT EXISTS `reduction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `quantite_vrac` int NOT NULL,
  `prix_vrac` double NOT NULL,
  `produit_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_reduction_produit` (`produit_id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reduction`
--

INSERT INTO `reduction` (`id`, `nom`, `quantite_vrac`, `prix_vrac`, `produit_id`) VALUES
(1, 'Lot de 3 Switch OLED', 3, 900, 6),
(2, 'Pack de 5 Redmi Note 12', 5, 1350, 5),
(3, 'Lot de 2 Bose 700', 2, 700, 8),
(4, 'Pack de 5 sacs Eastpak', 5, 150, 11),
(5, 'Lot de 3 Parfums Dior', 3, 180, 12),
(6, '2 Consoles PS5 + Réduction', 2, 1000, 10),
(7, 'TV Samsung - Lot de 2', 2, 1050, 7);

-- --------------------------------------------------------

--
-- Structure de la table `tab_admin`
--

DROP TABLE IF EXISTS `tab_admin`;
CREATE TABLE IF NOT EXISTS `tab_admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `prenom` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `MotDePasse` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `role` enum('SUPER_ADMIN','ADMIN') COLLATE utf8mb4_general_ci DEFAULT 'ADMIN',
  `date_inscription` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `tab_admin`
--

INSERT INTO `tab_admin` (`id`, `nom`, `prenom`, `email`, `MotDePasse`, `role`, `date_inscription`) VALUES
(1, 'admin_user', 'admin_user', '', '12345admin', 'ADMIN', '2025-03-23 09:55:57'),
(2, 'admin2', 'admin2', 'admin2', 'admin123', 'ADMIN', '2025-03-28 17:45:26');

-- --------------------------------------------------------

--
-- Structure de la table `tab_client`
--

DROP TABLE IF EXISTS `tab_client`;
CREATE TABLE IF NOT EXISTS `tab_client` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `prenom` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `MotDePasse` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `date_inscription` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `tab_client`
--

INSERT INTO `tab_client` (`id`, `nom`, `prenom`, `email`, `MotDePasse`, `date_inscription`) VALUES
(7, 'Cristiano', 'Ronaldo', 'cristiano.ronaldo@gmail.com', 'the2ndgoat', '2025-03-22 19:23:12'),
(10, 'Leo', 'Messi', 'leo.messi@gmail.com', 'thegoat', '2025-03-22 19:23:27'),
(12, 'Cmarshal', 'Gregouz', 'gregoire.marchal@gmail.com', '123456789+Gregoire', '2025-03-24 11:37:10'),
(13, 'tes1', 'tes1', 'tes1@gmail.com', 'test1', '2025-03-24 11:51:26'),
(77, 'test2', 'test2', 'test2@gmail.com', 'test2', '2025-03-24 12:16:42'),
(80, 'Populaire', 'Jeremy', 'jeremy.populaire@gmail.com', 'root', '2025-03-25 16:51:34'),
(81, 'Velasco', 'Angel', 'angel.velasco@gmail.com', 'angel123', '2025-03-28 17:32:40'),
(86, 'ssss', 's', 's', 's', '2025-03-31 08:59:06'),
(87, 'test', 'test', 'test', 'test', '2025-04-12 11:53:46'),
(88, 'abuelo', 'abuelo', 'abuelo@gmail.com', '123', '2025-04-12 13:26:58'),
(89, 'A', 'Mike', 'Mike', 'root', '2025-04-15 11:09:13'),
(90, 'mike', 'mike', 'root', 'root', '2025-04-27 12:07:48');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
