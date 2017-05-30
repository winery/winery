@echo off
echo Statistics generator for Winery EnPro 2016/2017
echo ================================================
echo .
echo Python 3.6 needs to be installed
echo This script needs to be run in the current git checkout of Winery.
echo The checkout has to be complete, i.e., no checkout with --depth 10
echo gitinspector has to reside in ..\..\gitinspector
echo You can clone it from https://github.com/ejwa/gitinspector
echo .
pause
py -3.6 ../../gitinspector/gitinspector.py -f java,html,ts,css --grading -x "author:^(?!((Lukas Harzenetter)|(Lukas Balzer)|(Nicole Keppler)|(Niko Stadelmaier)|(Tino Stadelmaier)|(Philipp Meyer)|(Huixin Liu)))" --format=htmlembedded> statistics/output.html
