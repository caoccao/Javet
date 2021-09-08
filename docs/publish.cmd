robocopy _build\_images _images /mir
robocopy _build\_static _static /mir
robocopy _build\html .\ /e /xd _sources /xd _images /xd _static /xf .buildinfo /xf objects.inv