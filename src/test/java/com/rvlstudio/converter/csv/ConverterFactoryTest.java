package com.rvlstudio.converter.csv;

import java.util.ArrayList;
import java.util.Arrays;
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
			private List<Pair> pairs = new ArrayList<>();

			private List<Sub> subs = new ArrayList<>();

			class Pair {
				String name;
				String value;
				Pair(String name, String value) { this.name = name; this.value = value; }

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}

				public String getValue() {
					return value;
				}

				public void setValue(String value) {
					this.value = value;
				}
				
			}
			
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
				pairs.add(new Pair("name", "value"));
				pairs.add(new Pair("naam", "waarde"));
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

			public List<Integer> getNummers() {
				return nummers;
			}

			public void setNummers(List<Integer> nummers) {
				this.nummers = nummers;
			}

			public List<Sub> getSubs() {
				return subs;
			}

			public void setSubs(List<Sub> subs) {
				this.subs = subs;
			}

			public List<Pair> getPairs() {
				return pairs;
			}

			public void setPairs(List<Pair> pairs) {
				this.pairs = pairs;
			}
			
		}

		Testing t1 = new Testing();
		Testing t2 = new Testing();
		t2.getNummers().add(4);
		t2.getNummers().add(5);
		t2.getNummers().add(6);
		t2.getLijst().add("second");
		CSV csv = ConverterFactory.creatConverter().registerTypeAdapter(new TypeAdapter<Testing.Pair>() {

					@Override
					public Cell createCell(Object o, String prefix) {
						return new Cell(){
							private String columnName = prefix + ((Testing.Pair)o).getName();
							private String data = ((Testing.Pair)o).getValue();
						
							@Override
							public boolean hasData() {
								return data != null;
							}
						
							@Override
							public String getData() {
								return data;
							}
						
							@Override
							public String getColumnName() {
								return columnName;
							}
						};
					}

					@Override
					public Class<?> getType() {
						return Testing.Pair.class;
					}
			
		}).convert(Arrays.asList(t1, t2));
		System.out.println(csv.toString());

	}
	
}