package com.currency.convertor;

import static org.junit.Assert.*;

import org.junit.Test;

import com.currency.convertor.Exception.ConversionRateExistsException;

import junit.framework.Assert;

public class ConversionHelperTest {
	
	@Test
	public void inrToUsd() throws ConversionRateExistsException {
		double rate = ConversionHelper.getRate("INR", "USD");
		Assert.assertEquals(0.0148310, rate);
	}
	
	@Test
	public void inrToJpy() throws ConversionRateExistsException {
		double rate = ConversionHelper.getRate("INR", "JPY");
		Assert.assertEquals(1.83335449893242, rate);
	}
	
	@Test
	public void inrToEur() throws ConversionRateExistsException {
		double rate = ConversionHelper.getRate("INR", "EUR");
		Assert.assertEquals(0.013236337361940001, rate);
	}

}
