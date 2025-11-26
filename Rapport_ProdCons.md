
# Rapport Technique – Projet Producteurs / Consommateurs  
**Polytech Grenoble – INFO4**  
**Cours : Applications Concurrentes**

## Introduction

Ce rapport présente une étude complète et détaillée d’un système de type Producteur–Consommateur, tel que demandé dans le cadre du projet du cours "Applications Concurrentes".  
L’objectif principal est de développer un tampon borné (buffer) permettant la communication entre des threads producteurs et consommateurs, en assurant la synchronisation, la cohérence, la sûreté d’accès, et des garanties strictes comme l’ordre FIFO.

Le travail couvre les objectifs suivants :  
- Objectif 1 : Solution directe (wait/notify)  
- Objectif 2 : Terminaison automatique  
- Objectif 3 : Synchronisation via Sémaphores  
- Objectif 4 : Synchronisation via Lock & Condition  
- Objectif 5 : Multi-consommation (get(k))  
- Objectif 6 : Multi-exemplaires synchrones (messages à n copies)  
- Objectif Additionnel : TaskExecutor non traité en entier

---

## Objectif 1 – Solution directe (wait/notify)

L'objectif consiste à implémenter un tampon borné en utilisant un tableau circulaire et la synchronisation primitive de Java via `wait()` / `notifyAll()`.

### Points essentiels :
- `put()` attend si le buffer est plein.  
- `get()` attend si le buffer est vide.  
- FIFO assuré via deux index circulaires `in` et `out`.  
- `notifyAll()` utilisé pour réveiller efficacement producteurs et consommateurs.

Cette solution constitue la base de toutes les versions suivantes.

---

## Objectif 2 – Terminaison automatique

Ici, il s’agissait de faire en sorte que l’application s’arrête automatiquement lorsque tous les messages ont été produits et consommés.
Le message terminating est affiché lorsque un consommateur a fini de consommer

### Stratégie :
- Avant de produire, chaque producteur déclare combien de messages il produira au total.  
- Le buffer calcule donc `totalToProduce`.  
- Chaque `get()` incrémente `totalConsumed`.  
- Lorsque `totalConsumed == totalToProduce` et que le buffer est vide, les consommateurs reçoivent un `null` et se terminent.  
- Le mécanisme permet une fin de programme propre, sans deadlocks.

---

## Objectif 3 – Version Sémaphores

Dans cette version, la synchronisation se fait via trois sémaphores :

- `empty` : nombre de places libres (initialisé à bufferSize).  
- `full` : nombre de places occupées.  
- `mutex` : exclusion mutuelle.

### Avantages :
- Réveil ciblé grâce à l'acquisition ou libération de sémaphores.  
- Parallélisme maximal pour le modèle producteur/consommateur.  

Cette version est souvent plus efficace que `wait/notify`.

---

## Objectif 4 – Locks & Conditions (optionnels)

L'utilisation de `ReentrantLock` et `Condition` permet une synchronisation fine et plus contrôlée. Les conditions distinctes permettent :
- `notFull.await()` pour les producteurs.  
- `notEmpty.await()` pour les consommateurs.  

### Bénéfices :
- Moins de réveils inutiles  
- Plus grande lisibilité  
- Flexibilité comparable à celle des sémaphores

---

## Objectif 5 – Multi-consommation (get(k))

Le buffer doit fournir une nouvelle opération :

```java
Message[] get(int k)
```

### Contraintes :
- Attendre que `nfull >= k`.  
- Récupérer les `k` messages consécutifs en FIFO.  
- Préserver la cohérence du tableau circulaire.  

Cette fonctionnalité ajoute de la complexité en raison de la gestion simultanée de plusieurs consommateurs. On peut avoir plsieurs cas selon les quantités demandé par un consommateur, et les quantités produits (voir code en java)

---

## Objectif 6 – Multi-exemplaires synchrones

Les producteurs peuvent déposer un message en **n exemplaires**, et **toutes les copies doivent être consommées par n threads distincts**.

### Comportements clés :
- Les consommateurs du même message doivent se synchroniser :  
  - Les `n` premiers consommateurs consomment le message.  
  - Tous attendent le dernier pour être libérés simultanément.
- Le producteur reste bloqué jusqu’à ce que toutes les copies soient consommées.  
- Le message n’est retiré du buffer qu’après la  n-ième consommation.

Ce mécanisme implémente une sorte de *barrière synchronisée par message*.

---

## Objectif Additionnel – TaskExecutor

Le but est de développer un système d’exécution de tâches semblable à un mini *ExecutorService* :

### Principes :
- Une tâche est un `Runnable`.  
- Un buffer similaire à ProdCons sert de file d’attente des tâches.  
- Les threads consommateurs sont gérés dynamiquement :  

### Intérêt :
- Approche professionnelle de gestion de threads  
- Efficace pour des applications modulaires et extensibles  
- Repose sur les concepts appris sur les buffers synchronisés

---

## Conclusion

Ce projet couvre un large spectre des mécanismes de synchronisation en Java, ce qui permet de :

- Comprendre profondément les modèles producteurs/consommateurs  
- Travailler avec plusieurs primitives de synchronisation (wait/notify, semaphores, locks/conditions)  
- Gérer des cas avancés (multi-consommation, exemplaires synchrones)  
- Construire un système réel (TaskExecutor) comparable à un thread pool industriel  

Ce travail prépare directement à la programmation concurrente avancée, essentielle dans les systèmes modernes tels que serveurs web, systèmes temps réel, moteurs d’événements et infrastructures multi-thread.

