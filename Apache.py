import socket
import os
import threading
import subprocess

# Dossier racine du serveur
ROOT_DIR = "./www"
PORT = 8080

def handle_client(client_socket):
    try:
        # Lire la requête
        request = client_socket.recv(1024).decode()
        if not request:
            client_socket.close()
            return

        # Extraire la méthode et le chemin demandé
        request_line = request.split("\r\n")[0]
        method, path, _ = request_line.split()

        # Chemin par défaut
        if path == "/":
            path = "/index.html"

        file_path = ROOT_DIR + path

        # Vérifier si le fichier existe
        if os.path.exists(file_path):
            if file_path.endswith(".php"):
                # Exécuter le fichier PHP avec PHP CLI
                output = subprocess.run(
                    ["php", file_path],
                    capture_output=True,
                    text=True
                )
                response_body = output.stdout
                content_type = "text/html"
            else:
                # Lire un fichier statique
                with open(file_path, "rb") as file:
                    response_body = file.read()
                content_type = "text/html" if file_path.endswith(".html") else "text/plain"

            # Réponse HTTP 200 OK
            response = (
                f"HTTP/1.1 200 OK\r\n"
                f"Content-Type: {content_type}\r\n"
                f"Content-Length: {len(response_body)}\r\n"
                f"\r\n"
            )
            client_socket.send(response.encode())
            if isinstance(response_body, str):
                client_socket.send(response_body.encode())
            else:
                client_socket.send(response_body)

        else:
            # Réponse HTTP 404 Not Found
            response_body = "<h1>404 Not Found</h1>"
            response = (
                "HTTP/1.1 404 Not Found\r\n"
                "Content-Type: text/html\r\n"
                f"Content-Length: {len(response_body)}\r\n"
                "\r\n"
                f"{response_body}"
            )
            client_socket.send(response.encode())
    except Exception as e:
        print(f"Erreur : {e}")
    finally:
        client_socket.close()

# Démarrer le serveur
def start_server():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(("0.0.0.0", PORT))
    server_socket.listen(5)
    print(f"Serveur démarré sur le port {PORT}...")

    while True:
        client_socket, addr = server_socket.accept()
        print(f"Connexion reçue de {addr}")
        # Gérer chaque client dans un thread séparé
        client_thread = threading.Thread(target=handle_client, args=(client_socket,))
        client_thread.start()

if __name__ == "__main__":
    start_server()
