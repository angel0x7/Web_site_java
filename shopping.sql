-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : ven. 21 mars 2025 à 09:11
-- Version du serveur : 8.0.31
-- Version de PHP : 8.0.26

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
                                      `titre` varchar(255) NOT NULL,
    `note` int NOT NULL,
    `description` varchar(10000) NOT NULL,
    `produit_id` int NOT NULL,
    `user_id` int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_avis_produit` (`produit_id`),
    KEY `fk_avis_user` (`user_id`)
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

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
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `marque`
--

DROP TABLE IF EXISTS `marque`;
CREATE TABLE IF NOT EXISTS `marque` (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `nom` varchar(255) NOT NULL,
    `image` varchar(255) NOT NULL,
    `description` varchar(10000) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `panier`
--

DROP TABLE IF EXISTS `panier`;
CREATE TABLE IF NOT EXISTS `panier` (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `utilisateur_id` int NOT NULL,
                                        `taille` int NOT NULL,
                                        PRIMARY KEY (`id`),
    KEY `fk_users` (`utilisateur_id`)
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `produit`
--

DROP TABLE IF EXISTS `produit`;
CREATE TABLE IF NOT EXISTS `produit` (
                                         `id` int NOT NULL AUTO_INCREMENT,
                                         `nom` varchar(255) NOT NULL,
    `image` varchar(255) NOT NULL,
    `marque_id` int NOT NULL,
    `prix` double NOT NULL,
    `quantite` int NOT NULL,
    `description` varchar(255) NOT NULL,
    `category` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_marque_produit` (`marque_id`)
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `reduction`
--

DROP TABLE IF EXISTS `reduction`;
CREATE TABLE IF NOT EXISTS `reduction` (
                                           `id` int NOT NULL AUTO_INCREMENT,
                                           `nom` varchar(255) NOT NULL,
    `quantite_vrac` int NOT NULL,
    `prix_vrac` double NOT NULL,
    `produit_id` int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_reduction_produit` (`produit_id`)
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `tab_admin` (
                             `id` int(11) NOT NULL,
                             `nom` varchar(50) NOT NULL,
                             `prenom` varchar(50) NOT NULL,
                             `email` varchar(100) NOT NULL,
                             `MotDePasse` varchar(255) NOT NULL,
                             `role` enum('SUPER_ADMIN','ADMIN') DEFAULT 'ADMIN',
                             `date_inscription` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

CREATE TABLE `tab_client` (
                              `id` int(100) NOT NULL,
                              `nom` varchar(100) DEFAULT NULL,
                              `prenom` varchar(100) DEFAULT NULL,
                              `email` varchar(255) DEFAULT NULL,
                              `MotDePasse` varchar(255) NOT NULL,
                              `date_inscription` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
                                                                                                (86, 'ssss', 's', 's', 's', '2025-03-31 08:59:06');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `tab_admin`
--
ALTER TABLE `tab_admin`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Index pour la table `tab_client`
--
ALTER TABLE `tab_client`
    ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `tab_admin`
--
ALTER TABLE `tab_admin`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `tab_client`
--
ALTER TABLE `tab_client`
    MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=87;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
