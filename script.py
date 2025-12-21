import os

def afficher_structure(dossier, niveau=0):
    try:
        fichiers = sorted(os.listdir(dossier))
    except PermissionError:
        print(" " * (niveau * 3) + "⛔ Accès refusé")
        return

    for f in fichiers:
        chemin = os.path.join(dossier, f)
        print(" " * (niveau * 3) + "|-- " + f)
        if os.path.isdir(chemin):
            afficher_structure(chemin, niveau + 1)

if __name__ == "__main__":
    dossier_racine = input("Entrez le chemin du dossier : ")
    print("\nStructure du dossier :\n")
    afficher_structure(dossier_racine)
