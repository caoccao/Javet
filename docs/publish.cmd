robocopy _build\html\_images _images /mir /nfl /ndl /njh /njs /nc /ns /np
robocopy _build\html\_sources _sources /mir /nfl /ndl /njh /njs /nc /ns /np
robocopy _build\html\_static _static /mir /nfl /ndl /njh /njs /nc /ns /np
robocopy _build\html .\ /e /xd _sources /xd _images /xd _static /xf .buildinfo /xf objects.inv /nfl /ndl /njh /njs /nc /ns /np