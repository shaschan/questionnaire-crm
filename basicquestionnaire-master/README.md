basic-complex-questionnaire-android
##	A custom complex questionnaire/quiz app on android

##Input questions
Currently reads from a `json` file in the *assets* folder.

##Type of questions
1. Fill in the blank - Simple text with blanks interspersed
2. Multiple choice with multiple selection - Using checkboxes
3. Multiple choice with single selection - Using radiobuttons
4. Image based single or multiple or text field selection
5. video based single or multiple or text field selection
6. conjoint based where, mutiple field uploaded via json file is processed to generate questionnaire


##Type of response
Currently string only.


##Input question format
*questions.json*
```json
{
  "questions": [
   {
	"QuesNum":1,
	"QuesText":"Serial Number?",
	"QuesType":"Plain Text",
	"blanks":"",
	"file":"",
	"totalOpts":0,
	"options":[]
   },
   {
	"QuesNum":2,
	"QuesText":"Question?",
	"QuesType":"Multiple Choice",
    "blanks":"",
	"file":"",
	"totalOpts":1,
	"options":[
				{
				 "opsNoKey":"1",
				 "opsTextKey":"optionXYZ",
				 "skipsinkey":["quesSinNo_1_2_1"],
				 "skipmulkey":["quesMulNo_1_1_1"],
				 "del12depkey":"N",
				 "del12undepkey":"Y"
				}
			  ]
	}
  ]
}
```
##Conjoint JSON file format
*conjoint.json*
```json
[	
	{
		"Version": "1",
		"Set": "1",
		"Item1": "Google",
		"Item2": "FireFox",
		"Item3": "Safari",
		"Item4": "Opera"
	},
	{
		"Version": "1",
		"Set": "2",
		"Item1": "Taxi",
		"Item2": "Bike",
		"Item3": "Auto",
		"Item4": "Cycle"
	},
	{
		"Version": "1",
		"Set": "3",
		"Item1": "Samsung",
		"Item2": "Apple",
		"Item3": "Sony",
		"Item4": "Nokia"
	}
]
```
##Response format
The responses are saved in a JSON mimicking the question format.
-- Courtsey : Vivek Pradhan (https://github.com/vivek1729)