# Reactive Instruments Workshop
An Activator template that can be used during Reactive Application workshops.

## Steps
Below are some hints for each step in the tutorial.

Each step has a corresponding git commit for peeps who just want to get to the code without pressing to many keys...

See `git branch` for the available steps.

To jump to a specific step `git checkout <step name>`

### Step 0 - Run Play
  * Two ways of working with this tutorial (I will show both):
    - 1: Use terminal windows + any IDE of your choice
    - 2: Use Activator UI to develop, test and run the tutorial
  * Start the Activator UI:
  
    `> <reactive-instrument-workshop-location>/activator ui`
  
  * Start your Play application and test endpoint http://localhost:9000
    - In Activator: Run the project by clicking on the “Run” tab and then “Run”
    - In Terminal:
    
     	`> activator run`
  
  * Update “John Doe” in file app/controllers/Application.scala to your name, save the file and refresh the browser

### Step 1 - Routes and Controllers
  
  * Create a controller in _app/controllers_ named InstrumentController with the method: 
      
      def index : a simple Action that just returns a text `<h1>Instruments</h1>`
  
  * Amend the conf/routes file so that /start points to the above index method
  * Start your Play application and test endpoint http://localhost:9000/start
    - In Activator: Run the project by clicking on the “Run” tab and then “Run”
    - In Terminal:
  
      `> activator run`

#### Credits
Background image in application from: 

- http://www.zingerbug.com/background.php?MyFile=mountain_landscape_with_lake_oregon_cascades_background_1800x1600.php&ID=C766.php
