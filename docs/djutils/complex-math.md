# Complex Math

Most mathematical operations on real numbers have a <i>compatible</i> implementation on complex numbers. <i>Compatible</i> in this case means that the result for an argument that happens to be real, matches the result of the real version of the function as long as the function has a defined result for the argument. There is a lot more to it. One way to derive <i>compatible</i> complex versions of such functions is to perform [Taylor series expansion](https://en.wikipedia.org/wiki/Taylor_series) and then use those with complex arguments.

## Principal square root

Similar to real numbers, the square root of a complex number is a complex number that, when multiplied to itself yields the orginal number. In the real number case, what we call the square root is actually the principal square root. E.g. the square root of 16 is 4. The other (non-principal) value that, when multiplied with itself yields 16 is -4. In the complex number space, there are also two solutions to the equation `s * s = z`. the principal square root of a complex number is the one that has the highest real component.

```java
    Complex z = new Complex(3, -5);
    System.out.println(z);
    System.out.println(ComplexMath.sqrt(z));
```

Outputs:

```text
Complex [re=3.0, im=-5.0]
Complex [re=2.101303392521568, im=-1.1897377641407583]
```

The other complex number that, when squared, yields the same result can be obtained by rotating by &pi;, or by negating (multiplying by `-1.0`). Ignoring rounding errors (due to the limited precision of the internal representation of &pi;) the results of negating or rotating by &pi; are identical. As the internal representation stores the real and imaginary components, negating is both faster and more accurate:

```java
    System.out.println(ComplexMath.sqrt(z).rotate(Math.PI));
    System.out.println(ComplexMath.sqrt(z).times(-1));
```

Outputs

```text
Complex [re=-2.101303392521568, im=1.1897377641407585]
Complex [re=-2.101303392521568, im=1.1897377641407583]
```


## Principal cube root

Unlike the real number space, the cube root of a complex number actually has three candidate values. The one with the highest real component is the principal cube root. The others can be obtained by rotating over `2 * π / 3` and  `4 * π / 3` :

```java
    System.out.println(ComplexMath.cbrt(z));
    System.out.println(ComplexMath.cbrt(z).rotate(2 * Math.PI / 3));
    System.out.println(ComplexMath.cbrt(z).rotate(4 * Math.PI / 3));
```

Outputs

```text
Complex [re=3.0, im=-5.0]
Complex [re=1.6947703899454605, im=-0.6061065307696287]
Complex [re=-0.32248154192657685, im=1.7707674766592425]
Complex [re=-1.3722888480188837, im=-1.1646609458896129]
```


## Exponential function

The exponential function raises Eulers constant `e` (about 2.7182818284590452354) to a specified power. The normal implementation takes a real argument. The implementation in `ComplexMath` takes a `Complex` argument:

```java
        Complex z = new Complex(3, -5);
        System.out.println(z);
        System.out.println(ComplexMath.exp(z));
```

Outputs:

```text
Complex [re=3.0, im=-5.0]
Complex [re=5.697507299833739, im=19.26050892528742]
```


## Natural logarithm

The inverse of the exponential function is the natural logarithm. In the java `Math` library, the natural logarithm function is named `log`. We find this utterly confusing and elected to name the equivalent function for complex numbers `ln`:

```java
    Complex z = new Complex(3, -5);
    System.out.println(z);
    System.out.println(ComplexMath.ln(z));
```

Outputs:

```text
Complex [re=3.0, im=-5.0]
Complex [re=1.7631802623080808, im=-1.0303768265243125]
```


## Sine, cosine, tangent

The trigonometric functions sine, cosine and tangent have counterparts in the complex domain:

```java
    Complex z = new Complex(3, -5);
    System.out.println(z);
    System.out.println(ComplexMath.sin(z));
    System.out.println(ComplexMath.cos(z));
    System.out.println(ComplexMath.tan(z));
```

Unlike these functions for real values; the results of sine and cosine are not limited to `[-1, +1]`:

```text
Complex [re=3.0, im=-5.0]
Complex [re=10.472508533940392, im=73.46062169567367]
Complex [re=-73.46729221264526, im=10.471557674805572]
Complex [re=-2.5368676207676027E-5, im=-0.9999128201513536]
```


## Inverse sine, inverse cosine, inverse tangent

The inverse functions are named `asin`, `acos` and `atan` (in literature these are sometimes named `arcsin`, `arccos` and `arctan`):

```java
    Complex z = new Complex(3, -5);
    System.out.println(z);
    System.out.println(ComplexMath.asin(z));
    System.out.println(ComplexMath.acos(z));
    System.out.println(ComplexMath.atan(z));
```

Outputs:

```text
Complex [re=3.0, im=-5.0]
Complex [re=0.5339990695941685, im=-2.4598315216234345]
Complex [re=1.036797257200728, im=2.4598315216234345]
Complex [re=1.4808695768986575, im=-0.14694666622552974]
```


## Hyperbolic sine, hyperbolic cosine, hyperbolic tangent

These hyperbolic functions are named `sinh`, `cosh` and `tanh`:

```java
    Complex z = new Complex(3, -5);
    System.out.println(z);
    System.out.println(ComplexMath.sinh(z));
    System.out.println(ComplexMath.cosh(z));
    System.out.println(ComplexMath.tanh(z));
```

Outputs:

```text
Complex [re=3.0, im=-5.0]
Complex [re=2.841692295606352, im=9.654125476854839]
Complex [re=2.855815004227387, im=9.606383448432581]
Complex [re=1.0041647106948153, im=0.002708235836224072]
```


## Inverse hyperbolic sine, inverse hyperbolic cosine, inverse hyperbolic tangent

Even though the inverse hyperbolic functions are not implemented in the java `Math` library we elected to add the complex versions to our `ComplexMath` library:
 
```java
    Complex z = new Complex(3, -5);
    System.out.println(z);
    System.out.println(ComplexMath.asinh(z));
    System.out.println(ComplexMath.acosh(z));
    System.out.println(ComplexMath.atanh(z));
```

Outputs:

```text
Complex [re=3.0, im=-5.0]
Complex [re=2.4529137425028096, im=-1.0238217465117878]
Complex [re=2.4598315216234345, im=-1.036797257200728]
Complex [re=0.08656905917945844, im=-1.4236790442393028]
```
