# Means package

The means package contains Java classes that can compute three different kinds of mean value:

* Arithmetic mean
* Geometric mean
* Harmonic mean

For an excellent discussion about when to use which of these three kinds of mean read "[On Average, You’re Using the Wrong Average: Geometric & Harmonic Means in Data Analysis](https://towardsdatascience.com/on-average-youre-using-the-wrong-average-geometric-harmonic-means-in-data-analysis-2a703e21ea0)" by [Daniel McNichol](https://towardsdatascience.com/@dnlmc). The writing of this package was inspired by that article.

To use this package, one must construct an `ArithmeticMean`, `GeometricMean`, or `HarmonicMean` object, specifying actual types for the generic V (value) type and for the generic W (weight) type. Then feed it values, or weighted values and finally (or repeatedly) obtain the resulting mean value with the getMean() method. There are a bunch of methods provided to feed data and these are all named add (but they have different method signatures):

<table class="docutils">
  <thead>
    <tr>
      <th width="30%">Method signature</th>
      <th width="70%">Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>add(V value)</code></td>
      <td>Add the value with weight 1</td>
    </tr>
    <tr>
      <td><code>add(V value, W weight)</code></td>
      <td>Add the value with weight <code>weight</code></td>
    </tr>
    <tr>
      <td><code>add(V[] values)</code></td>
      <td> Add all the items of the values array, each with weight 1</td>
    </tr>
    <tr>
      <td><code>add(V[] values, W[] weights)</code></td>
      <td>Add all the items of the values array, each with the corresponding weight from the weights array (throws <code>IllegalArgumentException</code> when the arrays do not have the same length)</td>
    </tr>
    <tr>
      <td><code>add(Iterable&lt;V&gt; values)</code></td>
      <td>Iterate over values and add each with weight 1</td>
    </tr>
    <tr>
      <td><code>add(Iterable&lt;V&gt; values, Iterable&lt;W&gt; weights)</code></td>
      <td>Iterate over values and simultaneously iterate over weights and add each value with the corresponding weight (throws <code>IllegalArgumentException</code> when the iterables do not yield the same number of items)</td>
    </tr>
    <tr>
      <td><code>add(Map&lt;V,W&gt; map)</code></td>
      <td>Iterate over the keys of the map and add those keys, each with a weight obtained by calling the <code>get</code> method of the map for that key (beware of name confusion: the keys of the map are the values in the accumulation and the values of the map are the weights used in the accumulation)</td>
    </tr>
    <tr>
      <td><code>add(Collection&lt;V&gt;, Function&lt;V, W&gt;)</code></td>
      <td>Add the values of the <code>Collection</code>, each with a weight obtained by invoking the provided function</td>
    </tr>
    <tr>
      <td><code>add(Collection&lt;S&gt;, Function&lt;S, V&gt; values, <br/> Function<S, W> weights)</code></td>
      <td>Iterate over the items in the <code>Collection</code> and obtain a value for each item in the collection by invoking the <code>values</code> function on the item and add it with a weight obtained by calling the <code>weights</code> function on the item</td>
    </tr>
  </tbody>
</table>


The accumulated results can be retrieved with:

<table class="docutils">
  <thead>
    <tr>
      <th width="30%">Method</th>
      <th width="70%">Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>public double getMean()</code></td>
      <td>Retrieve the mean (regrettably this returns a double; not a V)</td>
    </tr>
    <tr>
      <td><code>public double getSum()</code></td>
      <td>Retrieve the accumulated sum; the relation of this with the result of getMean depends on the type of mean (regrettably this returns a double; not a V)</td>
    </tr>
    <tr>
      <td><code>public double getSumOfWeights()</code></td>
      <td>Retrieve the accumulated weight (regrettably this returns a double; not a W)</td>
    </tr>
  </tbody>
</table>

The implementations of the three kinds of mean nicely illustrate the use of an abstract class that leaves the very minimum to be implemented to the classes that extend that to implement a particular kind of mean. The add methods that have Function arguments make use of a lambda expression (and, therefore, this package requires at least java version 8 to compile).
    