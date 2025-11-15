# <i><strong>klm-algorithms</strong></i> [![Deploy to Fly.io](https://github.com/chiefmonk/klm-algorithms/actions/workflows/main.yml/badge.svg)](https://github.com/chiefmonk/klm-algorithms/actions/workflows/main.yml)

This repository contains software developed as part of the dissertation '<i><strong>Implementation and Evaluation of KLM-Style Defeasible Entailment and Explanation Algorithms</strong></i>', submitted in fulfilment of the requirements for the degree of Master of Science in the Department of Computer Science, Faculty of Science, University of Cape Town.

### Table of Contents
1. [About the Tool](#about) 
2. [Requirements](#req)
3. [Compilation and Usage](#compile)
4. [Contributors](#cont)
5. [References](#refs)

<a name="about"></a>
## 1. About <i><strong>klm-algorithms</strong></i>
<i><strong>klm-algorithms</strong></i> is a modular software tool for computing <strong>defeasible entailment</strong> and <strong>justification-based explanations</strong> within the <strong>Kraus–Lehmann–Magidor (KLM)</strong> framework for non-monotonic reasoning. The tool implements the three principal KLM inference operators, <strong>Rational Closure</strong>, <strong>Lexicographic Closure</strong>, <strong>Basic Relevant Closure</strong>, and <strong>Minimal Relevant Closure</strong>, together with multiple optimised algorithmic variants for each operator.

The system provides a unified environment for:
- Constructing and editing propositional defeasible knowledge bases
- Defining defeasible statements
- Computing defeasible consequences under different KLM inference strategies
- Generating minimal justification sets, explaining why each conclusion holds
- Comparing algorithmic behaviour across operators and datasets

In addition to operator-specific justification procedures, the tool includes a <strong>universal justification algorithm</strong> that extracts all justifications from any deciding knowledge base, regardless of the inference operator that generated it.

Designed for both researchers and practitioners, KLM-Algorithms supports reproducible experimentation, scalable testing on synthetic and benchmark datasets, and visual exploration of entailments and explanations. It aims to provide a practical, extensible platform for studying defeasible reasoning, evaluating reasoning algorithms, and developing explainable AI systems grounded in formal logic.

<a name="req"></a>
## 2. Requirements
- Maven 4.0+
- Java 21+

<a name="compile"></a>
## 3. Compilation and Usage
There's a binary under `app/` folder which can be run without need for compilation. However, it still requires Java 21+. To run the binary run java -jar `app/klm-algorithms-1.0-SNAPSHOT.jar` and go to `http://localhost:8080/`.

### Compilation
```bash
mvn clean package
```
### Usage 
```bash
java -jar target/klm-algorithms-1.0-SNAPSHOT.jar
```
After running the above command, visit `http://localhost:8080/` on your web browser. Check the **syntax** before sending the queries.

### Live Deployment 
Alternately, you can just access the live web deployement at [https://klm-algorithms.fly.dev](https://klm-algorithms.fly.dev).

<a name="cont"></a>
## 4. Contributors
* [Chipo Hamayobe (chipo@cs.uct.ac.za)](https://github.com/chiefmonk) - Project Lead

<a name="refs"></a>
## 5. References
* ### The TweetyProject - [http://tweetyproject.org](http://tweetyproject.org)
The <strong>TweetyProject</strong> consists of diverse Java libraries that embody strategies for knowledge representation formalisms, encompassing classical logics, conditional logics, and probabilistic logics.

* ### The SAT4J SAT Solver - [http://www.sat4j.org](http://www.sat4j.org)
The <strong>SAT4J SAT Solver</strong> is a Java library designed to solve problems related to boolean satisfaction and optimisation.

* ### JUnit - [https://junit.org](https://junit.org)
The <strong>JUnit</strong> is a widely adopted open-source framework that provides a structured and organised approach to writing and running unit tests in Java applications.