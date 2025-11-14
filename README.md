<img src="images\uct_logo.png" width="100px" style="text-align:center;float:center;" />
# klm-algorithms [![Deploy to Fly.io](https://github.com/chiefmonk/klm-algorithms/actions/workflows/main.yml/badge.svg)](https://github.com/chiefmonk/klm-algorithms/actions/workflows/main.yml)

A defeasible reasoning tool implementing Rational Closure, Lexicographic Closure, Basic Relevant Closure and Minimal Relevant Closure.

### Table of Contents
1. [About the Tool](#about) 
2. [Requirements](#req)
3. [Compilation](#compile)
4. [Usage or Execution](#usage)
5. [Contributors](#cont)
6. [References](#refs)

<a name="about"></a>
## 1. About the <i><strong>klm-algorithmsL</strong></i>
<i><strong>klm-algorithms</strong></i> is a modular software tool for computing <strong>defeasible entailment</strong> and <strong>justification-based explanations</strong> within the Kraus–Lehmann–Magidor (KLM) framework for non-monotonic reasoning. The tool implements the three principal KLM inference operators—Rational Closure, Lexicographic Closure, and Relevant Closure—together with multiple optimised algorithmic variants for each operator.

The <strong>KLMDEETOOL</strong>, developed in Java, implements algorithms for KLM-style Rational Closure Entailment and Explanations. It consists of several interconnected components that work together to facilitate the processing and analysis of defeasible reasoning scenarios. The user interface tier offers both a command line interface (CLI) and a graphical user interface (GUI) for user interaction.

To aid in processing and solving complex logical expressions and queries efficiently, the <strong>KLMDEETOOL</strong> incorporates and relies on two
external libraries or packages: the <strong>TweetyProject</strong> and the <strong> SAT4J SAT Solver</strong>. The Desktop and Console Applications output of the <strong>KLMDEETOOL</strong> are shown below:

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