package util.filesystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Adapted from the java2s.com 'CreateJarFile' example
 */
public class JarCreator {
	
	public static int BUFFER_SIZE = 10240;
	
	/**
	 * Create a jar file, with optional manifest.
	 * @param jarName Name of output jar file to be created.
	 * @param basePath Base path to start searching for input files and the main class.
	 * @param fileNames Array of input file names (relative to basePath) to be added to the jar.
	 * @param mainClass Optionally, class file (relative to basePath) containing the main method.
	 */
	public void createStructuredExecutableJar(String jarName, String basePath,
			String[] fileNames, String mainClass) throws IOException {
		if (jarName == null || jarName.isEmpty()) 
			throw new IllegalArgumentException("Output jar file name not defined!");
		
		Manifest manifest = new Manifest();
		// Manifest is optional
		if (mainClass != null && !mainClass.isEmpty()) {
			if (mainClass.endsWith(".class")) {
				// Main-Class manifest entry should have no extension
				mainClass = mainClass.substring(0, mainClass.lastIndexOf('.'));
			}
			// Write out a specification-compliant manifest
			String manifestString = "Manifest-Version: 2.3" + System.getProperty("line.separator")
				+ "Main-Class: " + mainClass + System.getProperty("line.separator");
			InputStream manifestInput = new ByteArrayInputStream((manifestString).getBytes()); 
			try {
				manifest = new Manifest(manifestInput);
			} catch (IOException e) {
				// Highly unlikely, as we are constructing the manifest from a string!
			}
		}
		
		byte buffer[] = new byte[BUFFER_SIZE];
		// Open archive file
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(jarName);
		} catch (FileNotFoundException e) {
			throw new IOException("Cannot create or overwrite output jar file \"" + jarName + "\".", e);
		}
		JarOutputStream out = new JarOutputStream(stream, manifest);

		for (String fileName : fileNames) {
			if (fileName == null || fileName.isEmpty()) {
				System.out.println("Cannot add anonymous file to jar.");
				continue;
			}
			File fileToBeJarred = new File(basePath + "/" + fileName);
			if (!fileToBeJarred.exists() || fileToBeJarred.isDirectory()) {
				System.out.println("Cannot add \"" + fileToBeJarred.getPath() + "\" to jar - invalid file name.");
				continue;
			}
			System.out.println("Adding " + fileName);

			// Add archive entry
			JarEntry jarAdd = new JarEntry(fileName);
			jarAdd.setTime(fileToBeJarred.lastModified());
			out.putNextEntry(jarAdd);

			// Write file to archive
			FileInputStream in = new FileInputStream(fileToBeJarred);
			while (true) {
				int nRead = in.read(buffer, 0, buffer.length);
				if (nRead <= 0)
					break;
				out.write(buffer, 0, nRead);
			}
			in.close();
		}

		out.close();
		stream.close();
		System.out.println("Wrote " + jarName);
	}
}
