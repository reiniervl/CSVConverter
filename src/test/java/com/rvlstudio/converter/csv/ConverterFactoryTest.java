package com.rvlstudio.converter.csv;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * ConverterFactoryTest
 */
public class ConverterFactoryTest {

	@Test
	public void testFactory() {
		class Testing {
			private int a = 0;
			private String b = "hello";
			private List<String> lijst = new ArrayList<>();
			private List<Integer> nummers = new ArrayList<>();

			private List<Sub> subs = new ArrayList<>();
			
			class Sub {
				private String subString = "subby";
				private String subString1 = "subby1";

				public String getSubString() {
					return subString;
				}

				public void setSubString(String subString) {
					this.subString = subString;
				}

				public String getSubString1() {
					return subString1;
				}

				public void setSubString1(String subString1) {
					this.subString1 = subString1;
				}
				
			}

			Testing() {
				lijst.add("one");
				lijst.add("two");
				lijst.add("three");
				nummers.add(1);
				nummers.add(2);
				nummers.add(3);
				subs.add(new Sub());
				subs.add(new Sub());
			}

			public int getA() {
				return a;
			}

			public void setA(int a) {
				this.a = a;
			}

			public String getB() {
				return b;
			}

			public void setB(String b) {
				this.b = b;
			}

			public List<String> getLijst() {
				return lijst;
			}

			public void setLijst(List<String> lijst) {
				this.lijst = lijst;
			}

			public List<Sub> getSubs() {
				return subs;
			}

			public void setSubs(List<Sub> subs) {
				this.subs = subs;
			}

			public List<Integer> getNummers() {
				return nummers;
			}

			public void setNummers(List<Integer> nummers) {
				this.nummers = nummers;
			}
			
		}

		CSV csv = ConverterFactory.creatConverter(false).convert(new Testing());
		csv.getRows().forEach(r -> r.getCells().forEach(System.out::println));

	}
	
}