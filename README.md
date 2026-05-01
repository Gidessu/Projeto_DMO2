# 📱 Micro Rede Social Comunitária

Este projeto é uma **plataforma de rede social dinâmica** projetada para conectar pessoas e facilitar o compartilhamento de informações. 

Longe de ser apenas um diretório estático, esta aplicação funciona como um **feed em tempo real**, onde os usuários podem publicar conteúdos, visualizar postagens de outros membros e filtrar informações por localização.

Adicionalmente, o projeto utiliza o **Firebase** para garantir a persistência de dados e autenticação segura em nuvem.

---

## 🚀 Funcionalidades Principais

*   **Autenticação Segura:** Sistema de cadastro e login integrado ao **Firebase Auth**.
*   **Feed em Tempo Real:** Visualização de postagens com atualização instantânea via **Firestore Snapshot Listeners**.
*   **Filtro Inteligente:** Sistema de busca que ignora acentos e diferenciação entre maiúsculas/minúsculas para encontrar posts em locais específicos.
*   **Gestão de Perfil:** Personalização de perfil com foto (conversão Base64), nome de usuário e alteração de senha.
*   **Criação de Conteúdo:** Upload de posts com imagem, descrição detalhada e marcação automática de localização e autor.

---

## 🎯 Detalhes das Postagens

Cada publicação no feed é estruturada para fornecer informações rápidas e relevantes:

| Campo | Descrição e Objetivo |
| :--- | :--- |
| **Autor (@user)** | Identificação visual de quem publicou o conteúdo, exibida no topo do card. |
| **Localização** | Ponto de referência ou bairro para facilitar a filtragem regional. |
| **Descrição** | O "coração" do post: texto cativante que destaca a mensagem ou serviço. |
| **Imagem do Post** | Suporte visual para ilustrar o conteúdo compartilhado. |
| **Data** | Registro cronológico para manter o feed sempre atualizado. |

---

## ✨ Tecnologias

* **Tecnologias:** Desenvolvido em **Kotlin** utilizando **Android Studio**, com backend as a service (BaaS) provido pelo **Firebase (Firestore & Auth)**.

---

## 📸 Interface do Usuário

<div align="center">
  <img src="https://github.com/Gidessu/Projeto_DMO2/blob/main/Imagens/Login.png" width="200" alt="Login">
  <img src="https://github.com/Gidessu/Projeto_DMO2/blob/main/Imagens/Signin.png" width="200" alt="Sign in">
  <img src="https://github.com/Gidessu/Projeto_DMO2/blob/main/Imagens/Home.png" width="200" alt="Home">
  <img src="https://github.com/Gidessu/Projeto_DMO2/blob/main/Imagens/Profile.png" width="200" alt="Profile">
  <img src="https://github.com/Gidessu/Projeto_DMO2/blob/main/Imagens/Post.png" width="200" alt="Post">
  <img src="https://github.com/Gidessu/Projeto_DMO2/blob/main/Imagens/Search.png" width="200" alt="Search">
</div>

---

## 📺 Demonstração

**[🎥 Clique aqui para assistir ao vídeo do projeto em funcionamento](https://github.com/Gidessu/Projeto_DMO2/blob/main/Video/Showcase.webm)**

---
*Este projeto foi desenvolvido com foco no estudo de interfaces adaptativas e integração de serviços em nuvem no ambiente Android.*