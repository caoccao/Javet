robocopy _build\html\_images _images /mir /nfl /ndl /njh /njs /nc /ns /np
robocopy _build\html\_static _static /mir /nfl /ndl /njh /njs /nc /ns /np
robocopy ..\build\docs\javadoc reference\javadoc /mir /xf index.rst /nfl /ndl /njh /njs /nc /ns /np
robocopy _build\html .\ /e /xd _sources /xd _images /xd _static /xd javadoc /xf .buildinfo /xf objects.inv /nfl /ndl /njh /njs /nc /ns /np