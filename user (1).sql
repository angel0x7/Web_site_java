-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : lun. 24 mars 2025 à 12:36
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `user`
--

-- --------------------------------------------------------

--
-- Structure de la table `tab_admin`
--

CREATE TABLE `tab_admin` (
  `id` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `MotDePasse` varchar(255) NOT NULL,
  `role` enum('SUPER_ADMIN','ADMIN') DEFAULT 'ADMIN',
  `date_inscription` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `tab_admin`
--

INSERT INTO `tab_admin` (`id`, `nom`, `prenom`, `email`, `MotDePasse`, `role`, `date_inscription`) VALUES
(1, 'admin_user', 'admin_user', '', '12345admin', 'ADMIN', '2025-03-23 09:55:57');

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `tab_client`
--

INSERT INTO `tab_client` (`id`, `nom`, `prenom`, `email`, `MotDePasse`, `date_inscription`) VALUES
(7, 'Cristiano', 'Ronaldo', 'cristiano.ronaldo@gmail.com', 'the2ndgoat', '2025-03-22 19:23:12'),
(10, 'Leo', 'Messi', 'leo.messi@gmail.com', 'thegoat', '2025-03-22 19:23:27'),
(12, 'Cmarshal', 'Gregouz', 'gregoire.marchal@gmail.com', '123456789+Gregoire', '2025-03-24 11:37:10'),
(13, 'tes1', 'tes1', 'tes1@gmail.com', 'test1', '2025-03-24 11:51:26'),
(77, 'test2', 'test2', 'test2@gmail.com', 'test2', '2025-03-24 12:16:42');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `tab_client`
--
ALTER TABLE `tab_client`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=78;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
