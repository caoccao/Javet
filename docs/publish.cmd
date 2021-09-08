robocopy _build\html\_images _images /mir
robocopy _build\html\_sources _sources /mir
robocopy _build\html\_static _static /mir
robocopy _build\html .\ /e /xd _sources /xd _images /xd _static /xf .buildinfo /xf objects.inv