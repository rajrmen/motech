package org.motechproject.ScheduleTrackingDemo;

/**
 * Utility class to convert that converts an index number (e.g. 0, 1, etc.) to
 * its corresponding concept name.
 */
public class OpenMrsConceptConverter {

	private static final String[] CONCEPTS = { "Demo Concept Question #1",
			"Demo Concept Question #2", "Demo Concept Question #3",
			"Demo Concept Question #4"};
	
	public static String convertToNameFromIndex(int index) {
		if (index < 0 || index > 3) {
			throw new RuntimeException("Concept index must be between 0 and 3");
		}
		
		return CONCEPTS[index];
	}
	
	public static int convertToIndex(String conceptName) {
		int index = -1;
		for(int i = 0; i < CONCEPTS.length; i++) {
			if (CONCEPTS[i].equals(conceptName)) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	public static String getConceptBefore(String conceptName) {
		int index = convertToIndex(conceptName);
		
		if (index == -1) {
			throw new RuntimeException("Invalid concept name: " + conceptName);
		} else if (index == 0) {
			return conceptName;
		}
		
		index -= 1;
		return convertToNameFromIndex(index);
	}
}
