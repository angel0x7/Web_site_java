-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : dim. 13 avr. 2025 à 13:58
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
-- Base de données : `shopping`
--

-- --------------------------------------------------------

--
-- Structure de la table `avis`
--

CREATE TABLE `avis` (
  `id` int(11) NOT NULL,
  `titre` varchar(255) NOT NULL,
  `note` int(11) NOT NULL,
  `description` varchar(10000) NOT NULL,
  `produit_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `avis`
--

INSERT INTO `avis` (`id`, `titre`, `note`, `description`, `produit_id`, `user_id`) VALUES
(1, 'bien', 5, 'sa va ', 1, 87),
(2, 'gg', 7, 'jsp quoi dire', 2, 87),
(3, 'tttt', 5, 'yyy', 1, 87),
(4, 'yyyy', 5, 'yyy', 2, 87),
(5, 'hh', 5, 'jj', 2, 87),
(6, 'jj', 5, 'jjj', 1, 87),
(7, 'tres bien', 9, 'top', 2, 87),
(8, 'muy bien', 8, 'perfecto', 1, 88),
(9, 'bien', 8, 'top ', 2, 87),
(10, 'super', 5, 'top', 3, 87),
(11, 'c bien', 4, 'top', 2, 87),
(12, 'hh', 5, 'mj', 1, 87),
(13, 'trop topp', 4, 'super produit', 10, 87),
(14, 'trop', 4, 'super', 3, 87);

-- --------------------------------------------------------

--
-- Structure de la table `element_panier`
--

CREATE TABLE `element_panier` (
  `id` int(11) NOT NULL,
  `quantite` int(11) NOT NULL,
  `produit_id` int(11) NOT NULL,
  `panier_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `element_panier`
--

INSERT INTO `element_panier` (`id`, `quantite`, `produit_id`, `panier_id`) VALUES
(29, 1, 1, 13),
(38, 3, 6, 16),
(48, 1, 9, 18),
(27, 1, 3, 12),
(35, 1, 3, 15),
(32, 1, 3, 14),
(33, 1, 1, 14),
(34, 1, 2, 14),
(36, 1, 1, 15),
(53, 2, 10, 19),
(49, 1, 6, 18),
(62, 1, 3, 20),
(43, 1, 2, 17),
(44, 1, 9, 17),
(45, 1, 4, 17),
(46, 3, 6, 17),
(61, 1, 3, 11),
(50, 2, 10, 18),
(52, 3, 6, 19),
(60, 1, 7, 11),
(63, 2, 7, 20);

-- --------------------------------------------------------

--
-- Structure de la table `marque`
--

CREATE TABLE `marque` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `description` varchar(10000) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `panier`
--

CREATE TABLE `panier` (
  `id` int(11) NOT NULL,
  `utilisateur_id` int(11) NOT NULL,
  `taille` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `panier`
--

INSERT INTO `panier` (`id`, `utilisateur_id`, `taille`) VALUES
(11, 87, 2),
(12, 87, 1),
(13, 87, 1),
(14, 87, 3),
(15, 87, 2),
(16, 87, 1),
(17, 87, 4),
(18, 87, 3),
(19, 87, 2),
(20, 87, 2);

-- --------------------------------------------------------

--
-- Structure de la table `produit`
--

CREATE TABLE `produit` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `marque_id` int(11) NOT NULL,
  `prix` double NOT NULL,
  `quantite` int(11) NOT NULL,
  `description` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `produit`
--

INSERT INTO `produit` (`id`, `nom`, `image`, `marque_id`, `prix`, `quantite`, `description`, `category`) VALUES
(1, 'Iphone', 'iphone.png', 0, 1984, 5, 'tel taxé', 'electronique'),
(2, 'Samsung', 'telsamsung.png', 7, 500, 5, 'android', 'telephone'),
(3, '4 chevaux', '4cv.png', 44, 8000, 1, 'voiture', 'vehicule'),
(4, 'Macbook Air M1', 'macbook.png', 1, 1200, 10, 'Ordinateur portable léger', 'electronique'),
(5, 'Xiaomi Redmi Note 12', 'redmi.png', 2, 300, 50, 'Smartphone milieu de gamme', 'telephone'),
(6, 'Nintendo Switch OLED', 'switch.png', 3, 350, 20, 'Console de jeu portable', 'console'),
(7, 'TV Samsung 50’’ 4K', 'tv.png', 7, 600, 15, 'Télévision Smart 50 pouces', 'electronique'),
(8, 'Casque Bose 700', 'casque.png', 10, 400, 30, 'Casque réduction de bruit', 'accessoires'),
(9, 'Trottinette Xiaomi Pro', 'trot.png', 2, 500, 20, 'Trottinette électrique', 'mobilité urbaine'),
(10, 'Playstation 5 Standard', 'ps5.png', 8, 550, 25, 'Console de nouvelle génération', 'console');

-- --------------------------------------------------------

--
-- Structure de la table `reduction`
--

CREATE TABLE `reduction` (
  `id` int(11) NOT NULL,
  `nom` varchar(255) NOT NULL,
  `quantite_vrac` int(11) NOT NULL,
  `prix_vrac` double NOT NULL,
  `produit_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
(88, 'abuelo', 'abuelo', 'abuelo@gmail.com', '123', '2025-04-12 13:26:58');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `avis`
--
ALTER TABLE `avis`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_avis_produit` (`produit_id`),
  ADD KEY `fk_avis_user` (`user_id`);

--
-- Index pour la table `element_panier`
--
ALTER TABLE `element_panier`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_element_panier` (`panier_id`),
  ADD KEY `fk_produit_panier` (`produit_id`);

--
-- Index pour la table `marque`
--
ALTER TABLE `marque`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `panier`
--
ALTER TABLE `panier`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_users` (`utilisateur_id`);

--
-- Index pour la table `produit`
--
ALTER TABLE `produit`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_marque_produit` (`marque_id`);

--
-- Index pour la table `reduction`
--
ALTER TABLE `reduction`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_reduction_produit` (`produit_id`);

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
-- AUTO_INCREMENT pour la table `avis`
--
ALTER TABLE `avis`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT pour la table `element_panier`
--
ALTER TABLE `element_panier`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=64;

--
-- AUTO_INCREMENT pour la table `marque`
--
ALTER TABLE `marque`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `panier`
--
ALTER TABLE `panier`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT pour la table `produit`
--
ALTER TABLE `produit`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT pour la table `reduction`
--
ALTER TABLE `reduction`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pour la table `tab_admin`
--
ALTER TABLE `tab_admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `tab_client`
--
ALTER TABLE `tab_client`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=89;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
