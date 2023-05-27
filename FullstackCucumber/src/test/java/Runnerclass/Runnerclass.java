package Runnerclass;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)

@CucumberOptions(
		features = "src/test/java/Features",
		glue = {"Stepdefinations","hooks"},
		tags={"@assign"},
		monochrome = true,
		dryRun = false,
		plugin = {"pretty","html:target/htmlreport","json:target/report.json"}
		)

public class Runnerclass {
	
	

}
