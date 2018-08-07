package com.currency.convertor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.currency.convertor.Exception.ConversionRateExistsException;

public class ConversionHelper {

	public static double getRate(String fromCurrency,String toCurrency) throws ConversionRateExistsException {
		List<String> currencyList = new ArrayList<>();
		
		Map<String, HashMap<String, Double>> conversionRatesMap = getConversionRateList(currencyList);//reading the json and getting the map of various currency conversions.
		
		int size = conversionRatesMap.size();
		
		double min = Integer.MAX_VALUE;
		int nextNode = 0;
		double distance[] = new double[size]; // stores the calculated conversion rate for the source node to all the other nodes
		double[][] rate = new double[size][size];//stores the given conversion rates between the currencies
		boolean[] visited = new boolean[size];// stores whether the given node has been visited or not
		int in = currencyList.indexOf(fromCurrency); // get the index of the fromCurrency
		if(in!=0) {
			String temp = currencyList.get(0);
			currencyList.set(0, fromCurrency); //setting the fromCurrency as the source of the graph
			currencyList.set(in, temp);
		}
		int in1 = currencyList.indexOf(toCurrency);
		if(in1!=size-1) {
			String temp = currencyList.get(size-1);
			currencyList.set(size-1, toCurrency); //setting the toCurrency as the destination of the graph
			currencyList.set(in1, temp);
		}
		for(int i=0;i<size;i++) {
			rate[i][i] = 1.0; // conversion rate for the same currency will always be unity
			visited[i] = false; //initializing all the nodes as false initially
			String fromCurrency1 = currencyList.get(i); // the fromCurrency for the current loop
			HashMap<String, Double> tempMap = conversionRatesMap.get(fromCurrency1); // temporary map storing the available conversion rates for the fromCurrency for the available currencies
			for(int j=0;j<size;j++) {
				if(i!=j) {
					String toCurrency1 = currencyList.get(j);
					if(tempMap.containsKey(toCurrency1)) {
						rate[i][j] = tempMap.get(toCurrency1); //if the conversion exists using that value in the rate array
					}
					else {
						rate[i][j] = Integer.MAX_VALUE; //initializing to max if the conversion value doesn't exist.
					}
				}
			}
		}
		distance = rate[0]; //initializing the distance array as the rate of conversion for the fromCurrency.
		visited[0] = true; // making the fromCurrency node as visited
		distance[0]=1; //the rate ofconversion for the same currency would be 1
		for(int i=0;i<size;i++) {
			min = Integer.MAX_VALUE;
			for(int j=0;j<size;j++) {
				if(min>distance[j] && !visited[j]) { //if the node is not visited and the conversion rate is lower than the current minimum, then use the conversion rate and mark the node for further calculations 
					min = distance[j];
					nextNode =j;
				}
			}
			visited[nextNode] = true; //since the nodes conversion rate is used for calculation we mark the node as visited
			for(int j=0;j<size;j++) {
				if(!visited[j]) { //check for all the other unvisited nodes
					if(min*rate[nextNode][j] < distance[j]) { //if the value for the unvisited nodes is higher than the current min and conversion rate to that node we reassign the value for the current conversion node.
						distance[j] = min*rate[nextNode][j];
					}
				}
			}
		}
		System.out.println(rate[0][size-1]);
		return rate[0][size-1];//returning the rate for fromCurency(source) to toCurrency(destination)
	}

	private static Map<String, HashMap<String, Double>> getConversionRateList(List<String> currencyList)
			throws ConversionRateExistsException {
		JSONParser parser = new JSONParser();
		Map<String, HashMap<String, Double>> currencyRateMap = new HashMap<>();
		try {
			JSONArray a = (JSONArray) parser
					.parse(new FileReader("..\\Currency\\src\\main\\resources\\Assignemtn 1 Input.json"));
			for (int i = 0; i < a.size(); i++) {
				JSONObject object = (JSONObject) a.get(i);
				Double rate = (Double) object.get("rate");
				String toCurrency = (String) object.get("to_currency");
				String fromCurrency = (String) object.get("from_currency");
				/*Creating a map of fromCurrencies to all the toCurrencies and their conversion rates*/
				if (currencyRateMap.containsKey(fromCurrency)) {
					HashMap<String, Double> tempMap = currencyRateMap.get(fromCurrency);
					if (tempMap.containsKey(toCurrency)) {
						throw new ConversionRateExistsException("Conversion rate already exists");//not allowing duplicate conversion rates
					} else {
						tempMap.put(toCurrency, rate);
					}
				} else {
					currencyList.add(fromCurrency);
					HashMap<String, Double> tempMap = new HashMap<>();
					tempMap.put(toCurrency, rate);
					currencyRateMap.put(fromCurrency, tempMap);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currencyRateMap;
	}
}
