Longest Prefix Match
====================

Overview
--------
Longest Prefix Match is an implementation of an associative array which
doesn't associate keys to values using either the ```equals()``` or the
```hashCode()```. Instead it compares the beginning of a String with the
containing keys. The longest possible match will be associated and the value
be returned. That means that if a value with a key of length N was inserted,
any value which was inserted with a key of length N-X (where X > 0)
will only be a result of queries for matching strings with the length of N-Y
(where Y ≥ X).

Setup
-----

### sbt
```scala
libraryDependencies ++= Seq(
  "com.github.mkroli.lpm" %% "lpm" % "0.8.0"
)
```

### maven
```xml
<dependencies>
  <dependency>
    <groupId>com.github.mkroli.lpm</groupId>
    <artifactId>lpm_2.11</artifactId>
    <version>0.8.0</version>
  </dependency>
</dependencies>
```

Usage
-----

Longest Prefix Match is implemented using Scala. Nevertheless it provides
classes which allow an easy integration with Java, too.
The following sections describe how Longest Prefix Match can be used using
either Scala or Java.

### Scala
Lets pretend you want to retrieve a Int value according to a number
range. The two Int values being 1 and 2. 1 for "123" - "456" range and 2 for
"8" range. You'd add them as follows:
```scala
val lpm = new LongestPrefixMatch[Int].
  addValueForRange("123", "456", 1).
  addValueForRange("8", "8", 2).
  addValueForRange("80", "89", 3)
```
To retrieve the values you could do as follows:
```scala
lpm.getValueFromPrefix("8123") match {
  case Some(i) => println(i)
  case _ => println("Nothing found")
}
/* This would print 3 in conjunction with the previous statements */
```
Or shorter:
```scala
val lpm = new LongestPrefixMatch + ("123", "456", 1) + ("8", "8", 2) + ("80", "89", 3)
lpm("8123") match {
  case Some(i) => println(i)
  case _ => println("Nothing found")
}
```

### Java
The Java equivalent of the above example would look as follows:
```java
LongestPrefixMatchJ<Integer> lpm = new LongestPrefixMatchJ<Integer>();
lpm = lpm.addValueForRange("123", "456", 1);
lpm = lpm.addValueForRange("8", "8", 2);
lpm = lpm.addValueForRange("80", "89", 3);
```
And again to retrieve the values it would look as follows:
```java
Integer i = lpm.getValueFromPrefix("8123");
if(null == i)
	System.out.println("Nothing found");
else
	System.out.prinln(i);
```
