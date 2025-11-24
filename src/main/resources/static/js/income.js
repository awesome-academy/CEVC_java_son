function deleteFile(btn) {
    const csrfToken = window.CSRF_TOKEN; // Lấy từ global

    let fileId = btn.getAttribute('data-id');

    if (!confirm('Are you sure you want to delete this file?')) return;

    fetch('/attachments/delete/' + fileId, {
        method: 'DELETE',
        headers: {
            'X-CSRF-TOKEN': csrfToken
        }
    }).then(res => {
        if (res.ok) {
            btn.closest('li').remove();
        } else {
            alert('Failed to delete file.');
        }
    });
}
