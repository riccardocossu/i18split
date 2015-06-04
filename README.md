![](https://maven-badges.herokuapp.com/maven-central/net.riccardocossu.i18split/i18split-base/badge.svg "Maven central")

![](https://codeship.com/projects/81684260-e74e-0132-5eaa-1ed940dfa1b4/status?branch=master)

i18split
========

# Tools to manage i18n resources

Most non-trivial projects need some kind of internationalization (i18n) support at some point.
Java has a very good support for i18n, and the main tool is the use of properly named properties files, one per locale (messages_it.properties, messages_en.properties ....); it's a good practice to use this pattern to separate i18n from the rest of the code, but I think there is a drawback to face...

## What's the issue with multiple properties files?

To put it simply, it's hard to keep them in sync and even harder to check whether two files have the same key set; if you have more than a couple of files this becomes increasingly harder.

## Why not put all the locales in a single file?

Wouldn't it be nice to have everything in a single, text based file, with a line for each locale string key?
Yes, but we would have to roll out our own i18n system and have support from no one for it, unless we come up with something really good. But it's a lot of work anyway...

## i18split to the rescue

The idea behind i18split is that a CSV file is simple, vey well supported, easily convertable in common commercial formats, easily mergeable with any version control system and, most of all, can contain the same information of a hundred property files with the same keys!
Let me show you with an example:

<pre><code>
"key","it","en","es"
"hello","Ciao","Hello","Hola"
</code></pre>


is equivalent to three properties files with this content:

1. messages_it.properties 
<pre><code>hello=Ciao</code></pre>
2. messages_en.properties 
<pre><code>hello=Hello</code></pre>
3. messages_es.properties 
<pre><code>hello=Hola</code></pre>

That's it: the idea behind i18split is that a single CSV file can map multiple properties files and vice versa.

## Ok but in my jar (war, whatever) I need properties files!

It's your lucky day, it is trivial to integrate the i18split-maven plugin in your build!
If you have a CSV file called ValidationMessages.csv in the same format as my example, you can have your property files created in your project.build.directory (for example, but you're free to put them wherever you like), with this simple configuration!

```xml
<plugin>
				<groupId>net.riccardocossu.i18split</groupId>
				<artifactId>i18split-maven-plugin</artifactId>
				<version>1.0.0</version>
				<configuration>
					<inputBasePath>${basedir}/src/main/resources</inputBasePath>
					<outputBasePath>${project.build.directory}</outputBasePath>
				</configuration>
				<executions>
					<execution>
						<id>ValidationMessages</id>
						<phase>generate-resources</phase>
						<goals>
							<goal>convert</goal>
						</goals>
						<configuration>
							<inputPlugin>csv.input</inputPlugin>
							<outputPlugin>properties.output</outputPlugin>
							<pluginsConfig>
								<i18split.input.file>ValidationMessages.csv</i18split.input.file>
								<i18split.output.properties.fileName.prefix>ValidationMessages</i18split.output.properties.fileName.prefix>
							</pluginsConfig>
						</configuration>
					</execution>
			</executions>
</plugin>
```

# Cool, but my current project is using properties file since years so I'm stuck with those anyway. Maybe next project...

No, my friend, you can try in your current project, just use this configuration to produce the CSV you will use from now on!

```xml
<plugin>
				<groupId>net.riccardocossu.i18split</groupId>
				<artifactId>i18split-maven-plugin</artifactId>
				<version>1.0.0</version>
				<configuration>
					<inputBasePath>${basedir}/src/main/resources</inputBasePath>
					<outputBasePath>${project.build.directory}</outputBasePath>
				</configuration>
				<executions>
					<execution>
						<id>ValidationMessages</id>
						<phase>generate-resources</phase>
						<goals>
							<goal>convert</goal>
						</goals>
						<configuration>
						  <inputPlugin>properties.input</inputPlugin>
					    <outputPlugin>csv.output</outputPlugin>
							<pluginsConfig>
								<i18split.input.properties.file.name>ValidationMessages</i18split.input.properties.file.name>
								<i18split.input.properties.masterLocale>de</i18split.input.properties.masterLocale>
								<i18split.output.file>ValidationMessages.csv</i18split.output.file>
							</pluginsConfig>
						</configuration>
					</execution>
				</executions>
			</plugin>
```

# Sweet but... I need to share the output with someone who would prefer XLS or ODS

Why don't you check [i18split-export](https://github.com/riccardocossu/i18split-export) out?
Another lucky day for you!

## Time to try!

i18split-maven-plugin is on maven central so you don't need any more configuration to get started!
Please note that 18split requires at least Java 7 to run; it shouldn't be hard to backport it to Java 6 or lower if someone needs it.

## Detailed configuration

The plugin has a basic common configuration:

* *outputBasePath* where output files should be put
* *inputPlugin* short name for input plugin (see below)
* *outputPlugin* short name for output plugin (see below)
* *inputBasePath* base path for input files
* *inputEncoding* encoding for input files, it is ignored for properties files, unless they are in the xml format
* *outputEncoding* encoding for output files, ignored if they are classic (not xml) properties files
* *pluginsConfig* additional, non core, configuration; the main use is to configure arbitrary plugins

### Input plugins

All input plugin should support the standard property for input file:

*i18split.input.file* for single file inputs, this will have precedence over the specific plugin fileName

#### CsvInputDriver

Short name: *csv.input*

It reads a csv file; the first line will be the header, where the first column will contain the work "key" and the following will be locale names.; the number of locales will set the number of generated files.

Configuration:
* ~~*i18split.input.csv.fileName* name of csv input file~~ (deprecated, please use *i18split.input.file* instead)
* *i18split.input.csv.separator* separator to use for csv values
* *i18split.input.csv.quote* quote char to be used

#### PropertiesInputDriver

Short name: *properties.input*

It reads a set of property files with the usual convention of using the locale as a suffix for the name of the file.

Configuration:
* *i18split.input.properties.file.name* base file name for properties files (messages or so)
* *i18split.input.properties.isXml* tells if the source files are in xml format (default *false*)
* *i18split.input.properties.masterLocale* tells which locale contains the reference set of keys; this will be used to iterate the keys. If missing, all keys from all files will be used
* *i18split.default.column* the name of the default column (the one that is mapped on default properties file - the one without locale); this will be used by the output driver

### Output drivers

Configuration (common to all output drivers):
* *i18split.output.keepOrder* if true keys are written according to natural order (default: *false*)
* *i18split.output.file* output file for single file output plugins

#### CsvOutputDriver

Short name: *csv.output*

Writes a CSV file.

Configuration:
* ~~*i18split.output.csv.fileName* csv file name~~ (deprecated, please use *i18split.output.file* instead)
* *i18split.output.csv.separator* separator to use
* *i18split.output.csv.quote* quote to use
* *i18split.output.encoding* output file encoding

#### PropertiesOutputDriver

Short name: *properties.output*

Writes a set of properties file.

Configuration:
* *i18split.output.properties.fileName.prefix* output file names prefix (this typo has been fixed in revision 0.2.1 but you can still use deprecated old parameter for a while)
* *i18split.output.properties.isXml* whether to write the files as xml (default: false)
* *i18split.default.column* the name of the default column in the output; this will be mapped to the default properties files
