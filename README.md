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
				<version>0.1.1</version>
				<configuration>
					<inputBasePath>${basedir}/src/main/resources</inputBasePath>
					<outputBasePath>${project.build.directory}</outputBasePath>
				</configuration>
				<executions>
					<execution>
						<id>ValidationMessages</id>
						<phase>process-resources</phase>
						<goals>
							<goal>convert</goal>
						</goals>
						<configuration>
							<inputPlugin>csv.input</inputPlugin>
							<outputPlugin>properties.output</outputPlugin>
							<pluginsConfig>
								<i18split.input.csv.fileName>ValidationMessages.csv</i18split.input.csv.fileName>
								<i18split.output.properties.fileName.suffix>ValidationMessages</i18split.output.properties.fileName.suffix>
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
				<version>0.1.1</version>
				<configuration>
					<inputBasePath>${basedir}/src/main/resources</inputBasePath>
					<outputBasePath>${project.build.directory}</outputBasePath>
				</configuration>
				<executions>
					<execution>
						<id>ValidationMessages</id>
						<phase>process-resources</phase>
						<goals>
							<goal>convert</goal>
						</goals>
						<configuration>
						  <inputPlugin>properties.input</inputPlugin>
					    <outputPlugin>csv.output</outputPlugin>
							<pluginsConfig>
								<i18split.input.properties.file.name>ValidationMessages</i18split.input.properties.file.name>
								<i18split.input.properties.masterLocale>de</i18split.input.properties.masterLocale>
								<i18split.output.csv.fileName>ValidationMessages.csv</i18split.output.csv.fileName>
							</pluginsConfig>
						</configuration>
					</execution>
				</executions>
			</plugin>
```
