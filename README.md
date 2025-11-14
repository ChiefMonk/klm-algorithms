# <i><strong>klm-algorithms</strong></i> [![Deploy to Fly.io](https://github.com/chiefmonk/klm-algorithms/actions/workflows/main.yml/badge.svg)](https://github.com/chiefmonk/klm-algorithms/actions/workflows/main.yml)

<img src="images\uct_logo.png" width="100px" style="text-align:center;float:center;" />

## This repository contains software developed as part of the dissertation '<i><strong>Implementation and Evaluation of KLM-Style Defeasible Entailment and Explanation Algorithms</strong></i>', submitted in fulfilment of the requirements for the degree of Master of Science in the Department of Computer Science, Faculty of Science, University of Cape Town.

### Table of Contents
1. [About the Tool](#about) 
2. [Requirements](#req)
3. [Compilation](#compile)
4. [Usage or Execution](#usage)
5. [Contributors](#cont)
6. [References](#refs)

<a name="about"></a>
## 1. About the <i><strong>klm-algorithms</strong></i>
<i><strong>klm-algorithms</strong></i> is a modular software tool for computing <strong>defeasible entailment</strong> and <strong>justification-based explanations</strong> within the <strong>Kraus–Lehmann–Magidor (KLM)</strong> framework for non-monotonic reasoning. The tool implements the three principal KLM inference operators, <strong>Rational Closure</strong>, <strong>Lexicographic Closure</strong>, <strong>Basic Relevant Closure</strong>, and <strong>Minimal Relevant Closure</strong>, together with multiple optimised algorithmic variants for each operator.

The system provides a unified environment for:
- Maven 4.0+
- Java 20+
- Constructing and editing propositional knowledge bases
- Defining defeasible conditionals of the form $ \alpha \mid\!\sim \beta $
- Computing defeasible consequences under different KLM inference strategies
- Generating minimal justification sets, explaining why each conclusion holds
- Comparing algorithmic behaviour across operators and datasets

In addition to operator-specific justification procedures, the tool includes a <strong>universal justification algorithm</strong> capable of extracting all justifications from any deciding knowledge base, regardless of the inference operator that generated it.

Designed for both researchers and practitioners, KLM-Algorithms supports reproducible experimentation, scalable testing on synthetic and benchmark datasets, and visual exploration of entailments and explanations. It aims to provide a practical, extensible platform for studying defeasible reasoning, evaluating reasoning algorithms, and developing explainable AI systems grounded in formal logic.

## Screenshots 

![Query Inputs](data/images/app-formulas.png)

![Summary](data/images/app-summary.png)

![Base Rank](data/images/app-base.png)

![Rational Closure](data/images/app-rational.png)

![Lexicograhic Closure](data/images/app-lex.png)


## Requirements

* Maven 3.9.8+
* Java 21+

## Compilation & Usage
There's a binary under `app/` folder which can be run without need for compilation. However, it still requires Java 21+. To run the binary run java -jar `app/klm-algorithms-1.0-SNAPSHOT.jar` and go to `http://localhost:8080/`.
### Compilation
```bash
mvn clean package
```
### Usage 
```bash
java -jar target/klm-algorithms-1.0-SNAPSHOT.jar
```
After running the above command, visit `http://localhost:8080/` on your web browser. Chek the **Syntax** before sending the queries.