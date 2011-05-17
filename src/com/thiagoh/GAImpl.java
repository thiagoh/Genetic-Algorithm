package com.thiagoh;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GAImpl {

	private int min = -1;
	private int max = 2;
	private int numOfbits = 22;
	private double crossoverRate = 0.9;
	private double mutationRate = 0.05;
	private int populationLength;
	private int iterations;

	public GAImpl(int populationLength, int iterations) {

		this.populationLength = populationLength;
		this.iterations = iterations;
	}

	public String[] run() {

		String[] population = generatePopulationPhase(populationLength);

		for (int i = 0; i < iterations; i++) {

			population = evaluatePhase(population);

			population = crossoverPhase(population);

			population = mutationPhase(population);
		}

		return population;
	}

	private String[] evaluatePhase(String[] population) {

		int count = population.length;

		Double[] fitness = new Double[count];
		Map<Double, String> fitnessMapper = new HashMap<Double, String>();

		for (int i = 0; i < count; i++) {

			fitness[i] = f(map(population[i]));
			fitnessMapper.put(fitness[i], population[i]);
		}

		Arrays.sort(fitness, new Comparator<Double>() {

			public int compare(Double o1, Double o2) {
				return -1 * o1.compareTo(o2);
			}
		});

		String[] newPopulation = new String[count];
		int limit = (int) Math.ceil(count * 0.3);

		for (int populationCounter = 0; populationCounter < count; populationCounter++) {

			int index = 0;
			Random r = new Random();

			if (populationCounter > limit) {

				// seleciona um cromossomo dentre os mais aptos
				index = (int) Math.round(limit * r.nextDouble());

			} else {

				// seleciona um cromossomo dentre todos (para manter a
				// diversidade)
				index = (int) Math.round(count * r.nextDouble());
			}

			index = index < 0 ? 0 : index >= count ? count - 1 : index;

			newPopulation[populationCounter] = fitnessMapper.get(fitness[index]);
		}

		return newPopulation;
	}

	private String[] crossoverPhase(String[] population) {

		String[] newPopulation = new String[population.length];

		for (int i = 0; i < population.length; i += 2) {

			Random r = new Random();

			if (r.nextDouble() <= crossoverRate) {

				String[] childArray = crossover(population[i], population[i + 1]);

				newPopulation[i] = childArray[0];
				newPopulation[i + 1] = childArray[1];

			} else {

				newPopulation[i] = population[0];
				newPopulation[i + 1] = population[1];
			}
		}

		return newPopulation;
	}

	private String[] mutationPhase(String[] population) {

		String[] newPopulation = new String[population.length];

		for (int i = 0; i < population.length; i++) {

			Random r = new Random();

			String oldChromossome = population[i];
			StringBuilder newChromossome = new StringBuilder();

			for (int j = 0; j < oldChromossome.length(); j++) {

				if (r.nextDouble() <= mutationRate) {

					if (oldChromossome.charAt(j) == '1')
						newChromossome.append('0');
					else
						newChromossome.append('1');

				} else {

					newChromossome.append(oldChromossome.charAt(j));
				}
			}

			newPopulation[i] = newChromossome.toString();
		}

		return newPopulation;
	}

	private String[] crossover(String fatherChromossome1, String fatherChromossome2) {

		int pointCut = (int) Math.ceil(numOfbits / 2);

		String childChromossome1 = fatherChromossome1.substring(0, pointCut) + fatherChromossome2.substring(pointCut);
		String childChromossome2 = fatherChromossome2.substring(0, pointCut) + fatherChromossome1.substring(pointCut);

		return new String[] { childChromossome1, childChromossome2 };
	}

	private String[] generatePopulationPhase(int count) {

		String[] population = new String[count];

		Random r = new Random();

		for (int i = 0; i < population.length; i++) {

			population[i] = Long.toBinaryString(r.nextLong()).substring(0, 22);
		}

		return population;
	}

	private double f(double d) {

		// f(x) = xsen(10πx) + 1
		
		// http://www.wolframalpha.com/input/?i=plot+x+*+sin%2810+*+PI+*+x%29+%2B+1+from+x%3D-1+to+2
		// plot x * sin(10 * PI * x) + 1 from x=-1 to 2
		return d * Math.sin(10 * Math.PI * d) + 1;
	}

	private double map(String chromossome) {

		BigInteger bi = new BigInteger(chromossome, 2);

		long l = bi.longValue();

		return min + (max - min) * l / Math.pow(2, numOfbits);
	}

	public double getSolution(String[] population) {

		int count = population.length;

		Double[] fitness = new Double[count];
		Map<Double, String> fitnessMapper = new HashMap<Double, String>();

		for (int i = 0; i < count; i++) {

			fitness[i] = f(map(population[i]));
			fitnessMapper.put(fitness[i], population[i]);
		}

		Arrays.sort(fitness, new Comparator<Double>() {

			public int compare(Double o1, Double o2) {
				return -1 * o1.compareTo(o2);
			}
		});

		return map(fitnessMapper.get(fitness[0]));
	}

	public double evaluateSolution(String chromossome) {

		return f(map(chromossome));
	}

	public static void main(String[] args) {

		GAImpl ga = new GAImpl(1000, 10000);

		String[] population = ga.run();

		for (String chromossome : population) {

			System.out.println(chromossome + " = " + ga.evaluateSolution(chromossome));
		}

		System.out.println("\n\n\n");
		System.out.println("The solution is " + ga.getSolution(population));
	}
}
